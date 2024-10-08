package me.markoutte.joker.jsoupParse

import me.markoutte.joker.helpers.ComputeClassWriter
import me.markoutte.joker.jsoupParse.Grammar.ProbabilisticGrammarFuzzer
import me.markoutte.joker.jsoupParse.Grammar.probabilisticHtmlGrammar
import me.markoutte.joker.jsoupParse.Mutation.mutate
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.objectweb.asm.*
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.io.path.writeBytes
import kotlin.random.Random


@ExperimentalStdlibApi
fun main(args: Array<String>) {
    val options = Options().apply {
        addOption("c", "class", true, "Java class fully qualified name")
        addOption("m", "method", true, "Method to be tested")
        addOption("cp", "classpath", true, "Classpath with libraries")
        addOption("t", "timeout", true, "Maximum time for fuzzing in seconds")
        addOption("s", "seed", true, "The source of randomness")
    }
    val parser = DefaultParser().parse(options, args)
    val className = parser.getOptionValue("class")
    val methodName = parser.getOptionValue("method")
    val classPath = parser.getOptionValue("classpath")
    val timeout = parser.getOptionValue("timeout")?.toLong() ?: 10L
    val seed = parser.getOptionValue("seed")?.toInt() ?: Random.nextInt()
    val random = Random(seed)

    println("Running: $className.$methodName) with seed = $seed")
    val errors = mutableSetOf<String>()
    val start = System.nanoTime()

    val javaMethod = try {
        loadJavaMethod(className, methodName, classPath)
    } catch (t: Throwable) {
        println("Method $className#$methodName is not found")
        return
    }

    val seeds = mutableMapOf<Int, ByteArray>()
    val grammarFuzzer =
        ProbabilisticGrammarFuzzer(probabilisticHtmlGrammar(random), random)
    repeat(100) {
        seeds[-it] = grammarFuzzer.fuzz(
            maxExpansionTrials = 200,
            maxNumOfNonterminals = 1000
        )
    }

    while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(timeout)) {
        val buffer = mutate(random, seeds.values.random(random))
        val inputString = buffer.decodeToString()
        try {
            ExecutionPath.id = 0

            javaMethod.invoke(null, inputString).apply {
                seeds.putIfAbsent(ExecutionPath.id, buffer)
            }
        } catch (e: InvocationTargetException) {
            if (errors.add(e.targetException::class.qualifiedName!!)) {
                val errorName = e.targetException::class.simpleName
                println("New error found: $errorName")
                val path = Paths.get("report$errorName.txt")
                Files.write(
                    path, listOf(
                        "${e.targetException.stackTraceToString()}\n",
                        "${javaMethod.name}: $inputString\n",
                        "${buffer.contentToString()}\n"
                    )
                )
                println("Saved to: ${path.fileName}")
            }
        }
    }

    println("Seeds found: ${seeds.size}")
    println("Errors found: ${errors.size}")
    println(
        "Time elapsed: ${
            TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - start
            )
        } ms"
    )
}

fun loadJavaMethod(
    className: String,
    methodName: String,
    classPath: String
): Method {
    val libraries = classPath
        .split(File.pathSeparatorChar)
        .map { File(it).toURI().toURL() }
        .toTypedArray()
    val classLoader = object : URLClassLoader(libraries) {
        override fun loadClass(name: String, resolve: Boolean): Class<*> {
            return if (name.startsWith(className.substringBeforeLast('.'))) {
                transformAndGetClass(name).apply {
                    if (resolve) resolveClass(this)
                }
            } else {
                super.loadClass(name, resolve)
            }
        }

        fun transformAndGetClass(name: String): Class<*> {
            val owner = name.replace('.', '/')
            var bytes =
                getResourceAsStream("$owner.class")!!.use { it.readBytes() }
            val reader = ClassReader(bytes)
            val cl = this
            val writer = ComputeClassWriter(
                reader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES, cl
            )
            val transformer = object : ClassVisitor(Opcodes.ASM9, writer) {
                override fun visitMethod(
                    access: Int,
                    name: String?,
                    descriptor: String?,
                    signature: String?,
                    exceptions: Array<out String>?
                ): MethodVisitor {
                    return object : MethodVisitor(
                        Opcodes.ASM9,
                        super.visitMethod(
                            access, name, descriptor, signature, exceptions
                        )
                    ) {
                        val ownerName =
                            ExecutionPath.javaClass.canonicalName.replace('.', '/')
                        val fieldName = "id"

                        override fun visitLineNumber(line: Int, start: Label?) {
                            visitFieldInsn(
                                Opcodes.GETSTATIC, ownerName, fieldName, "I"
                            )
                            visitLdcInsn(line)
                            visitInsn(Opcodes.IADD)
                            visitFieldInsn(
                                Opcodes.PUTSTATIC, ownerName, fieldName, "I"
                            )
                            super.visitLineNumber(line, start)
                        }
                    }
                }
            }
            reader.accept(transformer, ClassReader.SKIP_FRAMES)
            bytes = writer.toByteArray().also {
                if (name == className) {
                    Paths.get("Instrumented.class").writeBytes(it)
                }
            }
            return defineClass(name, bytes, 0, bytes.size)
        }
    }
    val javaClass = classLoader.loadClass(className)
    val javaMethod = javaClass.declaredMethods.first {
        "${it.name}(${
            it.parameterTypes.joinToString(",") { c ->
                c.typeName
            }
        })" == methodName
    }
    return javaMethod
}


object ExecutionPath {
    @JvmField
    var id: Int = 0
}