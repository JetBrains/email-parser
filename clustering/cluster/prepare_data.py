import re


def get_headers_pairs_list(filename, verbose=False):
    """
    Read data for clustering from a given file.

    Data format: <email_number> <header>.
    Clusters are separated with a blank line.

    :param filename: file with input data for clustering.
    :param verbose: Whether to be verbose. Default - False.
    :return: List of pairs (email_number, header).

    """

    if verbose:
        print("Reading headers...", end="")

    with open(filename, encoding='utf-8') as inf:
        lines = list(map(lambda x: x.strip(), inf.readlines()))
        lines = list(filter(lambda x: x != "", lines))

        def make_pair(line):
            words = re.split(re.compile("\s+"), line)
            return (
                int(words[0]),
                " ".join(words[1:])
            )

        if verbose:
            print("Done")

        return list(map(make_pair, lines))


def get_labels(filename, verbose=False):
    """
    Determine true cluster label for a data from given file.

    Data format: <email_number> <header>.
    Clusters are separated with a blank line.

    :param filename:  file with input data for clustering
    :param verbose: Whether to be verbose. Default - False.
    :return: List of labels

    """

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


def get_affinity_matrix(dist_matrix, verbose=False, max_affinity=-1):
    if max_affinity == -1:
        raise Exception("max_affinity must be specified.")

    if verbose:
        print("Evaluating affinity matrix...", end="")

    affinity_matrix = [
        [max_affinity - elem for elem in line]
        for line in dist_matrix]

    if verbose:
        print("Done")

    return affinity_matrix


def write_dist_matrix(matrix, max_dist, filename, verbose=False):
    """
    Write distance matrix into a given file.

    :param matrix: Distance matrix.
    :param filename: File to write to.
    :param verbose: Whether to be verbose. Default - False.

    """

    if verbose:
        print("Writing to file...", end="")

    with open(filename, mode='w', encoding='utf-8') as ouf:
        ouf.write(str(max_dist) + "\n")
        for line in matrix:
            line = map(str, line)
            ouf.write(" ".join(line) + "\n")

    if verbose:
        print("Done")


def read_dist_matrix(filename, verbose=False):
    """
    Read a distance matrix from a given file.

    :param filename: File to read from.
    :param verbose: Whether to be verbose. Default - False.

    """

    if verbose:
        print("Reading distance matrix...", end="")

    with open(filename, mode='r', encoding='utf-8') as inf:
        lines = inf.readlines()
        maximum = int(lines[0].strip())
        # !!!
        matrix = list(map(lambda line: list(
            map(int, re.split(re.compile("\s+"), line.strip()))), lines[1:]))

        if verbose:
            print("Done")

        return matrix, maximum


def write_clusterized_data(filename, header_pair, labels, metrics=None,
                           verbose=False):
    """
    Write clustering results into a given file.

    Data format: <email_number> <header>.
    Clusters are separated with a blank line.

    If metrics variable is not None adds metrics to the end of the file.

    :param filename:  File to write to.
    :param header_pair: List of tuples (email_number, header).
    :param labels: Predicted clusters' labels.
    :param verbose: Whether to be verbose. Default - False.
    :param metrics: List of clusterization metrics or None.
    Has the following values if not None:
        [n_clusters,

        homogeneity_score,

        completeness_score,

        v_measure_score,

        adjusted_rand_score,

        adjusted_mutual_info_score,

        silhouette_score]

    """

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

            ouf.write(str(pair[0]) + " \t\t" + pair[1] + "\n")

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
