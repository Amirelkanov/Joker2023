from src.main.python.Mutation.mutation_techniques import *

mutators = [
    delete_random_character,
    insert_random_character,
    flip_random_character,
    shuffle_bytes
]


def mutate(s: str, rand: random.Random) -> bytearray:
    mutator = random.choice(mutators)
    return mutator(s, rand)
