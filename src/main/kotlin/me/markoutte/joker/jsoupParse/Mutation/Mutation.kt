package me.markoutte.joker.jsoupParse.Mutation

import kotlin.random.Random


val mutationTechniques = listOf(
    ::shuffleBuffPart,
    ::flipBit,
    ::changeByte,
    ::deleteByte
)

fun mutate(random: Random, buffer: ByteArray): ByteArray =
    mutationTechniques[random.nextInt(mutationTechniques.size)].invoke(
        random, buffer
    )
