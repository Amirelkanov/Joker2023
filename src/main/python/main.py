import random
import time
import traceback

import justext

import GrammarFuzzer
import HTMLGrammar

def main():
    timeout: int = 10
    seed: int = int(time.time())
    r = random.Random()

    print(f"Running: html5lib.parse with seed = {seed}")

    errors = set()
    start = time.time()

    grammar = HTMLGrammar.probabilistic_html_grammar(r)
    seeds = {-i: GrammarFuzzer.ProbabilisticGrammarFuzzer(grammar, r).fuzz(
        max_expansion_trials=200,
        max_num_of_nonterminals=500)
        for i in range(1000)}
    while (time.time() - start) < timeout:
        buf = mutate_string(r.choice(list(seeds.values())), 10, r)
        input_data = buf.decode('utf-8', errors='replace')
        try:
            justext.justext(input_data, justext.get_stoplist("English"))

            seeds[len(seeds) - 100 + 1] = input_data
        except Exception as e:
            error_name = type(e).__name__

            if error_name not in errors:
                errors.add(error_name)
                print(f"New error found: {error_name}")
                file = open(f"report{error_name}.txt", 'w', encoding="utf-8")

                file.write(''.join(traceback.TracebackException.from_exception(
                    e).format()) + "\n")
                file.write(input_data + "\n\n")
                file.write(str(list(buf)) + "\n")

                file.close()

    print(f"Seeds found: {len(seeds)}")
    print(f"Errors found: {len(errors)}")
    print(f"Time elapsed: {int((time.time() - start) * 1000)} ms")


def mutate_string(s: str, num_mutations: int, rand: random.Random) -> bytearray:
    byte_array = bytearray(s.encode('utf-8'))

    for _ in range(num_mutations):
        idx = rand.randint(0, len(byte_array) - 1)
        byte_array[idx] = rand.randint(0, 255)
    return byte_array


if __name__ == '__main__':
    main()
