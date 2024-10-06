import random
import re
from typing import List, Tuple

# Type alias for clarity
ProbabilisticGrammar = dict[str, list[tuple[str, float]]]


class ProbabilisticGrammarFuzzer:
    def __init__(self, grammar: ProbabilisticGrammar,
                 random_instance: random.Random):
        self.grammar = grammar
        self.random = random_instance

    def nonterminals(self, expansion: str) -> List[str]:
        return re.findall(r"\[[^\[\]]+\]", expansion)

    def select_probabilistic_expansion(self,
                                       expansions: List[Tuple[str, float]]) -> str:
        total_weight = sum(weight for _, weight in expansions)
        random_point = self.random.uniform(0, total_weight)

        cumulative_weight = 0.0
        for expansion, weight in expansions:
            cumulative_weight += weight
            if random_point <= cumulative_weight:
                return expansion
        return expansions[-1][
            0]  # Fallback to last expansion in case of rounding errors

    def fuzz(self, start_symbol: str = "[start]", max_num_of_nonterminals: int = 10,
             max_expansion_trials: int = 100, verbose: bool = False) -> str:
        term = start_symbol
        expansion_trials = 0

        while self.nonterminals(term):
            symbol_to_expand = random.choice(self.nonterminals(term))
            expansions = self.grammar.get(symbol_to_expand)

            if expansions is None:
                raise ValueError(f"Unknown symbol {symbol_to_expand}")

            expansion = self.select_probabilistic_expansion(expansions)
            new_term = term.replace(symbol_to_expand, expansion, 1)

            if len(self.nonterminals(new_term)) < max_num_of_nonterminals:
                term = new_term
                if verbose:
                    print(f"{symbol_to_expand} -> {expansion} : {term}")
                expansion_trials = 0  # Reset expansion trials on success
            else:
                expansion_trials += 1
                if expansion_trials >= max_expansion_trials:
                    break

        return term
