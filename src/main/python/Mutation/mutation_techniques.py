import random


def insert_random_character(s: str, rand: random.Random) -> bytearray:
    pos = rand.randint(0, len(s))
    random_character = chr(rand.randrange(32, 127))
    return bytearray((s[:pos] + random_character + s[pos:]).encode('utf-8'))


def flip_random_character(s, rand: random.Random):
    if s == "":
        return bytearray(s.encode('utf-8'))

    pos = rand.randint(0, len(s) - 1)
    c = s[pos]
    bit = 1 << rand.randint(0, 6)
    new_c = chr(ord(c) ^ bit)
    return bytearray((s[:pos] + new_c + s[pos + 1:]).encode('utf-8'))


def delete_random_character(s: str, rand: random.Random) -> bytearray:
    if s == "":
        return bytearray(s.encode('utf-8'))

    pos = rand.randint(0, len(s) - 1)
    return bytearray((s[:pos] + s[pos + 1:]).encode('utf-8'))


def shuffle_bytes(s: str, rand: random.Random) -> bytearray:
    buffer = bytearray(s, 'utf-8')

    if len(buffer) < 2:
        return buffer

    rand.shuffle(buffer)

    return buffer
