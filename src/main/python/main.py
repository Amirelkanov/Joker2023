import random
import time
import traceback

from pyquery import PyQuery as pq
from lxml import html
import GrammarFuzzer
import HTMLGrammar


def main():
    timeout: int = 50
    seed: int = int(time.time())
    r = random.Random()

    print(f"Running: html5lib.parse with seed = {seed}")

    errors = set()
    start = time.time()

    grammar = HTMLGrammar.probabilistic_html_grammar(r)
    seeds = {-i: GrammarFuzzer.ProbabilisticGrammarFuzzer(grammar, r).fuzz(
        max_expansion_trials=200,
        max_num_of_nonterminals=1000)
        for i in range(1000)}
    while 1:
        buf = mutate(r.choice(list(seeds.values())), r)
        input_data = buf.decode('utf-8', errors='replace')
        try:
            pq(input_data)
            seeds[len(seeds)] = input_data
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


def insert_random_character(s: str, rand: random.Random) -> bytearray:
    pos = rand.randint(0, len(s))
    random_character = chr(rand.randrange(32, 127))
    # print("Inserting", repr(random_character), "at", pos)
    return bytearray((s[:pos] + random_character + s[pos:]).encode('utf-8'))


def flip_random_character(s, rand: random.Random):
    """Returns s with a random bit flipped in a random position"""
    if s == "":
        return bytearray(s.encode('utf-8'))

    pos = rand.randint(0, len(s) - 1)
    c = s[pos]
    bit = 1 << rand.randint(0, 6)
    new_c = chr(ord(c) ^ bit)
    # print("Flipping", bit, "in", repr(c) + ", giving", repr(new_c))
    return bytearray((s[:pos] + new_c + s[pos + 1:]).encode('utf-8'))


def delete_random_character(s: str, rand: random.Random) -> bytearray:
    """Returns s with a random character deleted"""
    if s == "":
        return bytearray(s.encode('utf-8'))

    pos = rand.randint(0, len(s) - 1)
    # print("Deleting", repr(s[pos]), "at", pos)
    return bytearray((s[:pos] + s[pos + 1:]).encode('utf-8'))


import random


def shuffle_bytes(s: str, rand: random.Random) -> bytearray:
    buffer = bytearray(s, 'utf-8')

    if len(buffer) < 2:
        return buffer

    rand.shuffle(buffer)

    return buffer


def mutate(s: str, rand: random.Random) -> bytearray:
    """Return s with a random mutation applied"""
    mutators = [
        delete_random_character,
        insert_random_character,
        flip_random_character,
        shuffle_bytes
    ]
    mutator = random.choice(mutators)
    return mutator(s, rand)


if __name__ == '__main__':
    main()
