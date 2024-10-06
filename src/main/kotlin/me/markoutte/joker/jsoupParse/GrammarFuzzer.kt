package me.markoutte.joker.jsoupParse

typealias Grammar = Map<String, List<String>>


class GrammarFuzzer(val grammar: Grammar) {
    private fun nonterminals(expansion: String): List<String> {
        return Regex("\\[[^\\[\\]]+\\]").findAll(expansion).map { it.value }.toList()
    }

    fun generateInput(
        startSymbol: String = "[start]",
        maxNumOfNonterminals: Int = 100,
        maxExpansionTrials: Int = 100,
        verbose: Boolean = false
    ): String {
        var term = startSymbol
        var expansionTrials = 0

        while (nonterminals(term).isNotEmpty()) {
            val symbolToExpand = nonterminals(term).random()
            val expansions = grammar[symbolToExpand]
                ?: throw IllegalArgumentException("Unknown symbol $symbolToExpand")
            val expansion = expansions.random()
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
        return term
    }
}

