package me.markoutte.joker.jsoupParse

import kotlin.random.Random

typealias Grammar = Map<String, List<String>>

val htmlGrammar: Grammar = mapOf(
    "[start]" to listOf("<!DOCTYPE html>[html-element]"),
    "[html-element]" to listOf("<html>[head][body]</html>"),
    "[head]" to listOf("<head><title>[text]</title>[meta][style][script]</head>"),
    "[meta]" to listOf("<meta [attributes]>", "[eps]"),
    "[style]" to listOf("<style>[css]</style>", "[eps]"),
    "[script]" to listOf("<script>[js]</script>", "[eps]"),
    "[css]" to listOf(
        "body { margin: 0; padding: 0; }",
        "p { color: red; }",
        "h1 { font-size: 24px; }"
    ),
    "[js]" to listOf("console.log('[text]');", "alert('[text]');"),
    "[body]" to listOf("<body>[body-content]</body>"),
    "[body-content]" to listOf("[element]", "[element] [body-content]", "[eps]"),
    "[element]" to listOf(
        "[tag-element]",
        "[text-element]",
        "[comment]",
        "[list-element]",
        "[form-element]"
    ),
    "[tag-element]" to listOf("[opening-tag] [body-content] [closing-tag]"),
    "[opening-tag]" to listOf("<[tag-name] [attributes]>"),
    "[closing-tag]" to listOf("</[tag-name]>"),
    "[tag-name]" to listOf(
        "html",
        "head",
        "body",
        "div",
        "p",
        "a",
        "img",
        "span",
        "h1",
        "h2",
        "h3",
        "ul",
        "li",
        "form",
        "input",
        "button",
        "script",
        "style",
        "link",
        "meta",
        "table",
        "tr",
        "td",
        "th"
    ),
    "[attributes]" to listOf("[attribute]", "[attribute] [attributes]", "[eps]"),
    "[attribute]" to listOf("[attribute-name]=[attribute-value]"),
    "[attribute-name]" to listOf(
        "\"id\"",
        "\"class\"",
        "\"href\"",
        "\"src\"",
        "\"alt\"",
        "\"type\"",
        "\"name\"",
        "\"content\"",
        "\"style\"",
        "\"width\"",
        "\"height\"",
        "\"rel\""
    ),
    "[attribute-value]" to listOf(
        "\"[text]\"",
        "\"https://example.com\"",
        "\"#content\"",
        "\"button\"",
        "\"text\""
    ),
    "[text-element]" to listOf("[text]"),
    "[text]" to List(10) {
        randomStringByKotlinCollectionRandom(10, 40)
    },
    "[comment]" to listOf("<!-- [comment-text] -->"),
    "[comment-text]" to listOf("[text]", "[text]"),
    "[list-element]" to listOf("<ul>[list-items]</ul>"),
    "[list-items]" to listOf(
        "<li>[text]</li>",
        "<li>[text]</li>[list-items]",
        "[eps]"
    ),
    "[form-element]" to listOf("<form>[form-elements]</form>"),
    "[form-elements]" to listOf(
        "[input-element]",
        "[input-element][form-elements]",
        "[eps]"
    ),
    "[input-element]" to listOf("<input type=\"[input-type]\" name=\"[text]\" value=\"[text]\">"),
    "[input-type]" to listOf("text", "password", "email", "submit"),
    "[eps]" to listOf("")
)


fun nonterminals(expansion: String): List<String> {
    return Regex("\\[[^\\[\\]]+\\]").findAll(expansion).map { it.value }.toList()
}

fun grammarFuzzer(
    grammar: Grammar,
    startSymbol: String = "[start]",
    maxNonterminals: Int = 100,
    maxExpansionTrials: Int = 100,
    log: Boolean = false
): String {
    var term = startSymbol
    var expansionTrials = 0

    while (nonterminals(term).isNotEmpty()) {
        val symbolToExpand = nonterminals(term).random()
        val expansions = grammar[symbolToExpand]
            ?: throw IllegalArgumentException("Unknown symbol $symbolToExpand")
        val expansion = expansions.random()
        val newTerm = term.replaceFirst(symbolToExpand, expansion)

        if (nonterminals(newTerm).size < maxNonterminals) {
            term = newTerm
            if (log) {
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

fun randomStringByKotlinCollectionRandom(fromLength: Int, toLength: Int) =
    List(fromLength + Random.nextInt(toLength)) {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        charPool.random()
    }.joinToString("")
