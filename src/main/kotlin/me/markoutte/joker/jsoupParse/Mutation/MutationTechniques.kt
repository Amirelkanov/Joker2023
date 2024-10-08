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

fun flipRandomBit(random: Random, buffer: ByteArray) =
    buffer.clone().apply {
        val position = random.nextInt(0, size)
        val bitPosition = random.nextInt(0, 8)
        set(
            position,
            get(position).toInt().toChar().code.xor(1 shl bitPosition).toByte()
        )
    }

fun changeBytes(random: Random, buffer: ByteArray) = buffer.clone().apply {
    val position = random.nextInt(0, size)
    val repeat = random.nextInt((size - position))
    val from = random.nextInt(-128, 127)
    val until = random.nextInt(from + 1, 128)
    repeat(repeat) { i ->
        set(position + i, random.nextInt(from, until).toByte())
    }
}

fun deleteRandomBytes(random: Random, buffer: ByteArray): ByteArray {
    if (buffer.size <= 1) return buffer
    val numBytesToDelete = random.nextInt(1, buffer.size)
    val positionsToDelete = (buffer.indices).shuffled(random).take(numBytesToDelete).sortedDescending()
    return buffer.filterIndexed { index, _ -> index !in positionsToDelete }.toByteArray()
}
