import argparse
import random
import time
import traceback
from importlib import import_module

from src.main.python.Grammar import GrammarFuzzer, HTMLGrammar
from src.main.python.Mutation.mutation import mutate


# -mod AdvancedHTMLParser -c AdvancedHTMLParser -m parseStr
def main():
    parser = argparse.ArgumentParser(description="Fuzzing options")

    parser.add_argument("-mod", "--module_name", required=True,
                        help="Python module name")
    parser.add_argument("-c", "--class_name", required=True,
                        help="Python class name")
    parser.add_argument("-m", "--method_name", required=True,
                        help="Method to be tested")
    parser.add_argument("-t", "--timeout", type=int, default=10,
                        help="Maximum time for fuzzing in seconds")
    parser.add_argument("-s", "--seed", type=int, default=int(time.time()),
                        help="The source of randomness")

    args = parser.parse_args()
    module_name = args.module_name
    class_name = args.class_name
    method_name = args.method_name
    timeout = args.timeout
    seed = args.seed

    r = random.Random(seed)

    print(f"Running: {module_name}.{class_name}#{method_name} with seed = {seed}")

    try:
        python_method = load_python_method(module_name, method_name, class_name)
    except AttributeError:
        print(f"Method {module_name}.{class_name}#{method_name} has not found.")
        return
    errors = set()
    start = time.time()

    grammar = HTMLGrammar.probabilistic_html_grammar(r)
    seeds = {-i: GrammarFuzzer.ProbabilisticGrammarFuzzer(grammar, r).fuzz(
        max_expansion_trials=200,
        max_num_of_nonterminals=100)
        for i in range(1000)
    }
    while (time.time() - start) < timeout:
        buf = mutate(r.choice(list(seeds.values())), r)
        input_data = buf.decode('utf-8', errors='replace')
        try:
            python_method(input_data)
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


def load_python_method(module_name, method_name, class_name):
    module = import_module(module_name)
    python_method = getattr(
        getattr(module, class_name)() if class_name else module, method_name
    )
    return python_method


if __name__ == '__main__':
    main()
