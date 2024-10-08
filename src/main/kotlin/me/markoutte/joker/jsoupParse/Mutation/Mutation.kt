package me.markoutte.joker.jsoupParse.Mutation

import kotlin.random.Random


val mutationTechniques = listOf(
    ::shuffleBuffPart,
    ::flipRandomBit,
    ::changeBytes,
    ::deleteRandomBytes
)

fun mutate(random: Random, buffer: ByteArray): ByteArray =
    mutationTechniques[random.nextInt(mutationTechniques.size)].invoke(
        random, buffer
    )
