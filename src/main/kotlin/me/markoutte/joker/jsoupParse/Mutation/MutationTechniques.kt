package me.markoutte.joker.jsoupParse.Mutation

import kotlin.random.Random


fun shuffleBuffPart(random: Random, buffer: ByteArray) =
    buffer.clone().apply {
        val start = random.nextInt(0, size)
        val end = random.nextInt(start, size)
        val segment = sliceArray(start until end).toMutableList()
        segment.shuffle(Random)
        for (i in segment.indices) {
            set(start + i, segment[i])
        }
    }

fun flipBit(random: Random, buffer: ByteArray) =
    buffer.clone().apply {
        val position = random.nextInt(0, size)
        val bitPosition = random.nextInt(0, 8)
        set(
            position,
            get(position).toInt().toChar().code.xor(1 shl bitPosition).toByte()
        )
    }

fun changeByte(random: Random, buffer: ByteArray) = buffer.clone().apply {
    val position = random.nextInt(0, size)
    val from = random.nextInt(-128, 127)
    val until = random.nextInt(from + 1, 128)
    set(position, random.nextInt(from, until).toByte())
}

fun deleteByte(random: Random, buffer: ByteArray): ByteArray {
    if (buffer.isEmpty()) return buffer
    val position = random.nextInt(buffer.size)
    return buffer.filterIndexed { index, _ -> index != position }.toByteArray()
}