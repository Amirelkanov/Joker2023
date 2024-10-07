package me.markoutte.joker.jsoupParse

import java.nio.charset.Charset
import kotlin.random.Random

typealias ProbabilisticGrammar = Map<String, List<Pair<String, Double>>>

class ProbabilisticGrammarFuzzer(
    val grammar: ProbabilisticGrammar,
    val random: Random
) {

    private fun nonterminals(expansion: String): List<String> {
        return Regex("\\[[^\\[\\]]+\\]").findAll(expansion).map { it.value }.toList()
    }

    private fun selectProbabilisticExpansion(expansions: List<Pair<String, Double>>): String {
        val totalWeight = expansions.sumOf { it.second }
        val randomPoint = random.nextDouble(totalWeight)

        var cumulativeWeight = 0.0
        for (expansion in expansions) {
            cumulativeWeight += expansion.second
            if (randomPoint <= cumulativeWeight) {
                return expansion.first
            }
        }
        return expansions.last().first
    }

    fun fuzz(
        startSymbol: String = "[start]",
        maxNumOfNonterminals: Int,
        maxExpansionTrials: Int,
        charset: Charset = Charsets.UTF_8,
        verbose: Boolean = false
    ): ByteArray {
        var term = startSymbol
        var expansionTrials = 0

        while (nonterminals(term).isNotEmpty()) {
            val symbolToExpand = nonterminals(term).random()
            val expansions = grammar[symbolToExpand]
                ?: throw IllegalArgumentException("Unknown symbol $symbolToExpand")

            val expansion = selectProbabilisticExpansion(expansions)
            val newTerm = term.replaceFirst(symbolToExpand, expansion)

            if (nonterminals(newTerm).size < maxNumOfNonterminals) {
                term = newTerm
                if (verbose) {
                    println("$symbolToExpand -> $expansion : $term")
                }
                expansionTrials = 0
            } else {
                expansionTrials++
                if (expansionTrials >= maxExpansionTrials) {
                    break
                }
            }
        }
        return term.toByteArray(charset)
    }
}