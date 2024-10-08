package me.markoutte.joker.jsoupParse.Grammar

import kotlin.random.Random


fun probabilisticHtmlGrammar(random: Random): ProbabilisticGrammar = mapOf(
    "[start]" to listOf(
        "<!DOCTYPE html>[html-element]" to 1.0
    ),
    "[html-element]" to listOf(
        "<html>[head][body]</html>" to 1.0
    ),
    "[head]" to listOf(
        "<head><title>[text]</title>[meta][style][script]</head>" to 1.0
    ),
    "[meta]" to listOf(
        "<meta [attributes]>" to 0.8,
        "[eps]" to 0.2
    ),
    "[style]" to listOf(
        "<style>[css]</style>" to 0.5,
        "[eps]" to 0.5
    ),
    "[script]" to listOf(
        "<script>[js]</script>" to 0.5,
        "[eps]" to 0.5
    ),
    "[css]" to listOf(
        "body { margin: 0; padding: 0; }" to 0.3,
        "p { color: red; }" to 0.4,
        "h1 { font-size: 24px; }" to 0.3
    ),
    "[js]" to listOf(
        "console.log('[text]');" to 0.5,
        "alert('[text]');" to 0.5
    ),
    "[body]" to listOf(
        "<body>[body-content]</body>" to 1.0
    ),
    "[body-content]" to listOf(
        "[element]" to 0.3,
        "[element] [body-content]" to 0.6,
        "[eps]" to 0.1
    ),
    "[element]" to listOf(
        "[tag-element]" to 0.3,
        "[text-element]" to 0.2,
        "[comment]" to 0.1,
        "[list-element]" to 0.3,
        "[form-element]" to 0.1
    ),
    "[tag-element]" to listOf(
        "[opening-tag] [body-content] [closing-tag]" to 1.0
    ),
    "[opening-tag]" to listOf(
        "<[tag-name] [attributes]>" to 1.0
    ),
    "[closing-tag]" to listOf(
        "</[tag-name]>" to 1.0
    ),
    "[tag-name]" to listOf(
        "html" to 0.05,
        "head" to 0.05,
        "body" to 0.1,
        "div" to 0.1,
        "p" to 0.1,
        "a" to 0.1,
        "img" to 0.05,
        "span" to 0.05,
        "h1" to 0.05,
        "h2" to 0.05,
        "h3" to 0.05,
        "ul" to 0.05,
        "li" to 0.05,
        "form" to 0.05,
        "input" to 0.05,
        "button" to 0.05,
        "script" to 0.05,
        "style" to 0.05,
        "link" to 0.05,
        "meta" to 0.05,
        "table" to 0.05,
        "tr" to 0.05,
        "td" to 0.05,
        "th" to 0.05
    ),
    "[attributes]" to listOf(
        "[attribute]" to 0.1,
        "[attribute] [attributes]" to 0.7,
        "[eps]" to 0.2
    ),
    "[attribute]" to listOf(
        "[attribute-name]=[attribute-value]" to 1.0
    ),
    "[attribute-name]" to listOf(
        "\"id\"" to 0.1,
        "\"class\"" to 0.1,
        "\"href\"" to 0.1,
        "\"src\"" to 0.1,
        "\"alt\"" to 0.1,
        "\"type\"" to 0.1,
        "\"name\"" to 0.1,
        "\"content\"" to 0.1,
        "\"style\"" to 0.05,
        "\"width\"" to 0.05,
        "\"height\"" to 0.05,
        "\"rel\"" to 0.05
    ),
    "[attribute-value]" to listOf(
        "\"[text]\"" to 0.5,
        "\"https://example.com\"" to 0.2,
        "\"#content\"" to 0.2,
        "\"button\"" to 0.05,
        "\"text\"" to 0.05
    ),
    "[text-element]" to listOf(
        "[text]" to 1.0
    ),
    "[text]" to List(100) {
        randomStringByKotlinCollectionRandom(1, 100, random) to 0.01
    },
    "[comment]" to listOf(
        "<!-- [element] -->" to 0.2,
        "<!-- [text] -->" to 0.8,
    ),
    "[list-element]" to listOf(
        "<ul>[list-items]</ul>" to 1.0
    ),
    "[list-items]" to listOf(
        "<li>[text]</li>" to 0.5,
        "<li>[text]</li>[list-items]" to 0.4,
        "[eps]" to 0.1
    ),
    "[form-element]" to listOf(
        "<form>[form-elements]</form>" to 1.0
    ),
    "[form-elements]" to listOf(
        "[input-element]" to 0.5,
        "[input-element][form-elements]" to 0.4,
        "[eps]" to 0.1
    ),
    "[input-element]" to listOf(
        "<input type=\"[input-type]\" name=\"[text]\" value=\"[text]\">" to 1.0
    ),
    "[input-type]" to listOf(
        "text" to 0.4,
        "password" to 0.3,
        "email" to 0.2,
        "submit" to 0.1
    ),
    "[eps]" to listOf(
        "" to 1.0
    )
)

fun randomStringByKotlinCollectionRandom(
    fromLength: Int,
    toLength: Int,
    random: Random
) =
    List(fromLength + random.nextInt(toLength)) {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        charPool.random()
    }.joinToString("")