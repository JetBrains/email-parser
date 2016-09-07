import copy
from random import Random
import time
import inspyred
import sys
import os
from inspyred.ec.utilities import memoize

from optimization.ap_optim import costs_uniform_crossover

sys.path.append(os.getcwd())

from cluster.prepare_data import get_headers_pairs_list
from cluster.token import token_type, Token
from executable.mst_clustering import clustering

MAX_COST = 200
MIN_COST = 10


def generate_costs(random, args):
    size = len(token_type)
    ins_cost = \
        [int(random.uniform(MIN_COST, MAX_COST)) for _ in range(size)]
    repl_cost = \
        [[int(random.uniform(MIN_COST, MAX_COST)) for _ in range(i + 1)] for
         i in range(size-1)]
    colon_inequality_cost = int(random.uniform(MIN_COST, MAX_COST))
    return [ins_cost, repl_cost, colon_inequality_cost]


def setup_costs(ins_cost, repl_matr, colon_cost):
    Token.INSERTION_COST["UNDEFINED"] = ins_cost[0]
    Token.INSERTION_COST["DATE_RELATED"] = ins_cost[1]
    Token.INSERTION_COST["DAY"] = ins_cost[2]
    Token.INSERTION_COST["YEAR"] = ins_cost[3]
    Token.INSERTION_COST["DATE_SHORT"] = ins_cost[4]
    Token.INSERTION_COST["TIME"] = ins_cost[5]
    Token.INSERTION_COST["EMAIL"] = ins_cost[6]

    repl_m = copy.deepcopy(repl_matr)
    for line in repl_m:
        line.append(0)
    repl_m.insert(0, [0])
    Token.REPLACEMENT_COST = repl_m

    Token.LAST_COLON_INEQUALITY_COST = colon_cost

    sum = colon_cost
    for line in repl_matr:
        for x in line:
            sum += x
    for x in ins_cost:
        sum += x

    return sum // 29


@memoize
def evaluate_costs(candidates, args):
    fitness = []
    headers = args.get("headers", -1)
    if headers == -1:
        raise Exception("No headers for clustering.")
    cs_len = len(candidates)
    for i, cs in enumerate(candidates):
        average_cost = setup_costs(cs[0], cs[1], cs[2])
        fit = clustering(headers, average_cost)
        if str(fit) == "nan":
            fitness.append(-1)
        fitness.append(fit)

        print("Evaluated {0}/{1}: {2} : {3}".format(i+1, cs_len, cs,
                                                    fitness[i]))

    return fitness


def bound_costs(candidate, args):
    (ins_cost, repl_matr, colon_cost) = candidate
    for i, e in enumerate(ins_cost):
        ins_cost[i] = max(min(e, MAX_COST), MIN_COST)
    for i, line in enumerate(repl_matr):
        for j, e in enumerate(line):
            repl_matr[i][j] = max(min(e, MAX_COST), MIN_COST)
    colon_cost = max(min(colon_cost, MAX_COST), MIN_COST)
    candidate[0] = ins_cost
    candidate[1] = repl_matr
    candidate[2] = colon_cost
    return candidate


def mutate_costs(random, candidates, args):
    mut_rate = args.get('mutation_rate', 0.1)
    mut_distance = args.get('mutation_distance', 0.1)
    diff = (MAX_COST - MIN_COST) * mut_distance

    count = 0
    for i, cs in enumerate(candidates):
        (ins_cost, repl_matr, colon_cost) = cs
        if random.random() < mut_rate:
            count += 1
            for k in range(len(ins_cost)):
                ins_cost[k] += int(random.gauss(0, 1) * diff)
            for k, line in enumerate(repl_matr):
                for j in range(len(line)):
                    repl_matr[k][j] += int(random.gauss(0, 1) * diff)
            colon_cost += int(random.gauss(0, 1) * diff)
        candidates[i][2] = colon_cost
        candidates[i] = bound_costs(candidates[i], args)
    print("{0}/{1} mutations occurred.".format(count, len(candidates)))
    return candidates


def main(dataset_filename):
    start = time.perf_counter()

    rand = Random()
    rand.seed(int(time.time()))
    headers = \
        list(map(lambda x: x[1], get_headers_pairs_list(dataset_filename)))

    my_ec = inspyred.ec.EvolutionaryComputation(rand)
    my_ec.selector = inspyred.ec.selectors.tournament_selection
    my_ec.variator = [inspyred.ec.variators.crossover(costs_uniform_crossover),
                      mutate_costs]
    my_ec.replacer = inspyred.ec.replacers.truncation_replacement
    my_ec.observer = [inspyred.ec.observers.file_observer,
                      inspyred.ec.observers.stats_observer]
    my_ec.terminator = inspyred.ec.terminators.evaluation_termination

    final_pop = my_ec.evolve(generator=generate_costs,
                             evaluator=evaluate_costs,
                             bounder=bound_costs,
                             tournament_size=5,
                             headers=headers,
                             statistics_file=open(
                                 "stats_mst_trunc_half_1.csv", "w"),
                             individuals_file=open(
                                 "inds_mst_trunc_half_1.csv", "w"),
                             # --- customizable arguments ---
                             pop_size=60,
                             num_selected=30,
                             mutation_rate=0.75,
                             mutation_distance=0.15,
                             max_evaluations=1860
                             # ------------------------------
                             )

    # Sort and print the best individual, who will be at index 0.
    final_pop.sort(reverse=True)
    print('Terminated due to {0}.'.format(my_ec.termination_cause))
    print(final_pop[0])

    end = time.perf_counter()
    print("\nWorking time: %f sec." % (end - start))

    inspyred.ec.analysis.generation_plot(open("stats_mst_trunc_half_1.csv"),
                                         errorbars=False)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Too few arguments. You should provide: dataset_filename")
        sys.exit()
    dataset_filename = sys.argv[1]
    main(dataset_filename)
