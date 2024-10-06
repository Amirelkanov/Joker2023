import random
import string
from typing import List, Dict, Tuple

# Type alias for clarity
ProbabilisticGrammar = dict[str, list[tuple[str, float]]]


def probabilistic_html_grammar(
        random_instance: random.Random) -> ProbabilisticGrammar:
    return {
        "[start]": [
            ("<!DOCTYPE html>[html-element]", 1.0)
        ],
        "[html-element]": [
            ("<html>[head][body]</html>", 1.0)
        ],
        "[head]": [
            ("<head><title>[text]</title>[meta][style][script]</head>", 1.0)
        ],
        "[meta]": [
            ("<meta [attributes]>", 0.8),
            ("[eps]", 0.2)
        ],
        "[style]": [
            ("<style>[css]</style>", 0.5),
            ("[eps]", 0.5)
        ],
        "[script]": [
            ("<script>[js]</script>", 0.5),
            ("[eps]", 0.5)
        ],
        "[css]": [
            ("body { margin: 0; padding: 0; }", 0.3),
            ("p { color: red; }", 0.4),
            ("h1 { font-size: 24px; }", 0.3)
        ],
        "[js]": [
            ("console.log('[text]');", 0.5),
            ("alert('[text]');", 0.5)
        ],
        "[body]": [
            ("<body>[body-content]</body>", 1.0)
        ],
        "[body-content]": [
            ("[element]", 0.3),
            ("[element] [body-content]", 0.6),
            ("[eps]", 0.1)
        ],
        "[element]": [
            ("[tag-element]", 0.3),
            ("[text-element]", 0.2),
            ("[comment]", 0.1),
            ("[list-element]", 0.3),
            ("[form-element]", 0.1)
        ],
        "[tag-element]": [
            ("[opening-tag] [body-content] [closing-tag]", 1.0)
        ],
        "[opening-tag]": [
            ("<[tag-name] [attributes]>", 1.0)
        ],
        "[closing-tag]": [
            ("</[tag-name]>", 1.0)
        ],
        "[tag-name]": [
            ("html", 0.05),
            ("head", 0.05),
            ("body", 0.1),
            ("div", 0.1),
            ("p", 0.1),
            ("a", 0.1),
            ("img", 0.05),
            ("span", 0.05),
            ("h1", 0.05),
            ("h2", 0.05),
            ("h3", 0.05),
            ("ul", 0.05),
            ("li", 0.05),
            ("form", 0.05),
            ("input", 0.05),
            ("button", 0.05),
            ("script", 0.05),
            ("style", 0.05),
            ("link", 0.05),
            ("meta", 0.05),
            ("table", 0.05),
            ("tr", 0.05),
            ("td", 0.05),
            ("th", 0.05)
        ],
        "[attributes]": [
            ("[attribute]", 0.1),
            ("[attribute] [attributes]", 0.7),
            ("[eps]", 0.2)
        ],
        "[attribute]": [
            ("[attribute-name]=[attribute-value]", 1.0)
        ],
        "[attribute-name]": [
            ("\"id\"", 0.1),
            ("\"class\"", 0.1),
            ("\"href\"", 0.1),
            ("\"src\"", 0.1),
            ("\"alt\"", 0.1),
            ("\"type\"", 0.1),
            ("\"name\"", 0.1),
            ("\"content\"", 0.1),
            ("\"style\"", 0.05),
            ("\"width\"", 0.05),
            ("\"height\"", 0.05),
            ("\"rel\"", 0.05)
        ],
        "[attribute-value]": [
            ("\"[text]\"", 0.5),
            ("\"https://example.com\"", 0.2),
            ("\"#content\"", 0.2),
            ("\"button\"", 0.05),
            ("\"text\"", 0.05)
        ],
        "[text-element]": [
            ("[text]", 1.0)
        ],
        "[text]": [
            (random_string(1, 100, random_instance), 0.01) for _ in range(100)
        ],
        "[comment]": [
            ("<!-- [comment-text] -->", 1.0)
        ],
        "[comment-text]": [
            ("[text]", 1.0)
        ],
        "[list-element]": [
            ("<ul>[list-items]</ul>", 1.0)
        ],
        "[list-items]": [
            ("<li>[text]</li>", 0.5),
            ("<li>[text]</li>[list-items]", 0.4),
            ("[eps]", 0.1)
        ],
        "[form-element]": [
            ("<form>[form-elements]</form>", 1.0)
        ],
        "[form-elements]": [
            ("[input-element]", 0.5),
            ("[input-element][form-elements]", 0.4),
            ("[eps]", 0.1)
        ],
        "[input-element]": [
            ("<input type=\"[input-type]\" name=\"[text]\" value=\"[text]\">", 1.0)
        ],
        "[input-type]": [
            ("text", 0.4),
            ("password", 0.3),
            ("email", 0.2),
            ("submit", 0.1)
        ],
        "[eps]": [
            ("", 1.0)
        ]
    }


def random_string(from_length: int, to_length: int,
                  random_instance: random.Random) -> str:
    length = random_instance.randint(from_length, to_length)
    char_pool = string.ascii_letters + string.digits
    return ''.join(random_instance.choice(char_pool) for _ in range(length))
