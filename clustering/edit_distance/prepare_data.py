import re


def get_headers_pairs_list(filename):
    with open(filename, encoding='utf-8') as inf:
        lines = list(map(lambda x: x.strip(), inf.readlines()))
        lines = list(filter(lambda x: x != "", lines))

        def make_pair(line):
            words = re.split(re.compile("\s+"), line)
            return (
                int(words[2]),
                " ".join(words[3:])
            )

        return list(map(make_pair, lines))


def get_labels(filename):
    with open(filename, encoding='utf-8') as inf:
        lines = list(map(lambda x: x.strip(), inf.readlines()))
        cluster_id = 0
        labels = []
        for idx in range(len(lines)):
            if lines[idx] == "":
                if lines[idx - 1] != "":
                    cluster_id += 1
                continue
            labels.append(cluster_id)
        return labels


def get_affinity_matrix(dist_matrix):
    max_affinity = 1000000
    affinity_matrix = [[max_affinity if elem == 0 else max_affinity / elem for elem in line] for line in dist_matrix]
    return affinity_matrix


def write_dist_matrix(matrix, filename):
    with open(filename, mode='w', encoding='utf-8') as ouf:
        for line in matrix:
            line = map(str, line)
            ouf.write(" ".join(line) + "\n")


def read_dist_matrix(filename):
    with open(filename, mode='r', encoding='utf-8') as inf:
        lines = inf.readlines()
        # !!!
        matrix = list(map(lambda line: list(map(int, re.split(re.compile("\s+"), line.strip()))), lines))
        return matrix









