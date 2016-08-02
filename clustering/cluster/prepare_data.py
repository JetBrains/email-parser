import re


def get_headers_pairs_list(filename, verbose=False):
    if verbose:
        print("Reading headers...", end="")

    with open(filename, encoding='utf-8') as inf:
        lines = list(map(lambda x: x.strip(), inf.readlines()))
        lines = list(filter(lambda x: x != "", lines))

        def make_pair(line):
            words = re.split(re.compile("\s+"), line)
            return (
                int(words[2]),
                " ".join(words[3:])
            )

        if verbose:
            print("Done")

        return list(map(make_pair, lines))


def get_labels(filename, verbose=False):
    if verbose:
        print("Forming labels...", end="")

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

        if verbose:
            print("Done")

        return labels


def get_affinity_matrix(dist_matrix, verbose=False, max_affinity=1000000):
    if verbose:
        print("Evaluating affinity matrix...", end="")

    affinity_matrix = [[max_affinity if elem == 0 else max_affinity / elem for elem in line] for line in dist_matrix]

    if verbose:
        print("Done")

    return affinity_matrix


def write_dist_matrix(matrix, filename, verbose=False):
    if verbose:
        print("Writing to file...", end="")

    with open(filename, mode='w', encoding='utf-8') as ouf:
        for line in matrix:
            line = map(str, line)
            ouf.write(" ".join(line) + "\n")

    if verbose:
        print("Done")


def read_dist_matrix(filename, verbose=False):
    if verbose:
        print("Reading distance matrix...", end="")

    with open(filename, mode='r', encoding='utf-8') as inf:
        lines = inf.readlines()
        # !!!
        matrix = list(map(lambda line: list(map(int, re.split(re.compile("\s+"), line.strip()))), lines))

        if verbose:
            print("Done")

        return matrix


def write_clusterized_data(filename, header_pair, labels, metrics=None, verbose=False):
    if verbose:
        print("Writing clusterized data...", end="")

    zipped = list(zip(header_pair, labels))
    zipped.sort(key=lambda x: x[1])

    with open(filename, mode='w', encoding='utf-8') as ouf:
        cluster_id = 0
        for i, triple in enumerate(zipped):
            pair = triple[0]
            label = triple[1]
            if label != cluster_id:
                cluster_id += 1
                ouf.write('\n')

            ouf.write(str(pair[0]) + " -\t\t" + pair[1] + "\n")

        if metrics is not None:

            ouf.write('\n')
            ouf.write('Estimated number of clusters: %d\n' % metrics[0])
            ouf.write("Homogeneity: %0.3f\n" % metrics[1])
            ouf.write("Completeness: %0.3f\n" % metrics[2])
            ouf.write("V-measure: %0.3f\n" % metrics[3])
            ouf.write("Adjusted Rand Index: %0.3f\n" % metrics[4])
            ouf.write("Adjusted Mutual Information: %0.3f\n" % metrics[5])
            ouf.write("Silhouette Coefficient: %0.3f\n" % metrics[6])

    if verbose:
        print("Done")


def print_metrics(metrics):
    print()
    print('Estimated number of clusters: %d' % metrics[0])
    print("Homogeneity: %0.3f" % metrics[1])
    print("Completeness: %0.3f" % metrics[2])
    print("V-measure: %0.3f" % metrics[3])
    print("Adjusted Rand Index: %0.3f" % metrics[4])
    print("Adjusted Mutual Information: %0.3f" % metrics[5])
    print("Silhouette Coefficient: %0.3f" % metrics[6])
    print()
