import sys
import os

sys.path.append(os.getcwd())

import time
import numpy as np
from sklearn.cluster import AffinityPropagation
from sklearn import metrics
from cluster.prepare_data import get_labels, get_affinity_matrix, \
    read_dist_matrix, write_clusterized_data, print_metrics, setup_costs
from cluster.prepare_data import get_headers_pairs_list

from cluster.token_edit_distance import get_distance_matrix
from sklearn.utils.sparsetools._traversal import connected_components, \
    csr_matrix

# -------------
__parent = dict()
__rank = dict()


def __make_set(vertice):
    __parent[vertice] = vertice
    __rank[vertice] = 0


def __find(vertice):
    if __parent[vertice] != vertice:
        __parent[vertice] = __find(__parent[vertice])
    return __parent[vertice]


def __union(vertice1, vertice2):
    root1 = __find(vertice1)
    root2 = __find(vertice2)
    if root1 != root2:
        if __rank[root1] > __rank[root2]:
            __parent[root2] = root1
        else:
            __parent[root1] = root2
            if __rank[root1] == __rank[root2]:
                __rank[root2] += 1


def __kruskal(graph):
    for vertice in range(graph['vertices_num']):
        __make_set(vertice)

    minimum_spanning_tree = set()
    edges = list(graph['edges'])
    edges.sort()
    for edge in edges:
        weight, vertice1, vertice2 = edge
        if __find(vertice1) != __find(vertice2):
            __union(vertice1, vertice2)
            minimum_spanning_tree.add(edge)
    return minimum_spanning_tree


def get_mst(distance_matrix):
    edges = set()
    for i, line in enumerate(distance_matrix):
        for j, e in enumerate(line):
            if j < i:
                edges.add((distance_matrix[i][j], i, j))
    graph = {
        'vertices_num': len(distance_matrix),
        'edges': edges
    }
    return __kruskal(graph)


def get_cluster(mst_, min_dist=10):
    if min_dist <= 0:
        raise Exception("min_dist must be positive, but {0}".format(min_dist))
    mst_sorted = sorted(mst_, key=lambda x: x[0])
    idx = len(mst_sorted)
    for i in range(len(mst_sorted)):
        if mst_sorted[i][0] > min_dist:
            idx = i
            break
    return mst_sorted[:idx]


def get_labels_predict(edges, n):
    row = []
    col = []
    data = []
    for (cost, from_, to_) in edges:
        row.append(from_)
        col.append(to_)
        data.append(cost)
        row.append(to_)
        col.append(from_)
        data.append(cost)
    csr_graph = csr_matrix((data, (row, col)), shape=(n, n))
    return connected_components(csr_graph, directed=False)


def fit(dist_matr, min_dist=10):
    mst = get_mst(dist_matr)
    conn_components = get_cluster(mst, min_dist)
    n_clusters, labels = get_labels_predict(conn_components, len(dist_matr))
    return n_clusters, labels


# --------


def clustering(headers, min_dist, distance_matrix_filename=None):
    if distance_matrix_filename is None:
        dist_matrix, _ = get_distance_matrix(headers)
    else:
        dist_matrix, _ = read_dist_matrix(distance_matrix_filename)

    n_clusters_, labels = fit(dist_matrix, min_dist)

    return metrics.silhouette_score(np.asmatrix(dist_matrix), labels,
                                    metric='precomputed')


def main(dataset_filename, output_data_filename,
         distance_matrix_filename=None, display=False, min_dist=10):
    start = time.perf_counter()

    headers_pairs = get_headers_pairs_list(dataset_filename, verbose=True)
    labels_true = get_labels(dataset_filename, verbose=True)

    if distance_matrix_filename is None:
        dist_matrix, max_dist = get_distance_matrix(list(map(lambda x: x[1],
                                                             headers_pairs)),
                                                    verbose=True)
    else:
        dist_matrix, max_dist = \
            read_dist_matrix(distance_matrix_filename, verbose=True)

    print("Clustering...")
    n_clusters_, labels = fit(dist_matrix, min_dist)
    print("Done.")

    print("clusters {0}".format(n_clusters_))
    print(labels)

    metrics_list = [
        n_clusters_,
        metrics.homogeneity_score(labels_true, labels),
        metrics.completeness_score(labels_true, labels),
        metrics.v_measure_score(labels_true, labels),
        metrics.adjusted_rand_score(labels_true, labels),
        metrics.adjusted_mutual_info_score(labels_true, labels),
        metrics.silhouette_score(np.asmatrix(dist_matrix), labels,
                                 metric='precomputed')
    ]

    print_metrics(metrics_list)

    write_clusterized_data(output_data_filename, headers_pairs, labels,
                           metrics=metrics_list, verbose=True)

    end = time.perf_counter()
    print("\nWorking time: %f sec." % (end - start))

    # if display:
    #     visualize(dist_matrix, labels, cluster_centers_indices,
    #               show_cluster_sizes=True)


def get_average(params):
    (ins_cost, repl_matr, colon_cost) = params
    sum = colon_cost
    for line in repl_matr:
        for x in line:
            sum += x
    for x in ins_cost:
        sum += x

    return sum // 29

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(
            "Too few arguments. You should provide: \n1. dataset_filename" +
            "\n2. output_data_filename \n3. distance_matrix_filename (optional)"
        )
        sys.exit()
    dataset_filename_ = sys.argv[1]
    output_data_filename_ = sys.argv[2]
    distance_matrix_filename_ = sys.argv[3] if len(sys.argv) > 3 else None

    params = [[177, 164, 97, 78, 20, 42, 163],
                 [[53], [60, 186], [43, 97, 88], [141, 182, 14, 48],
                  [195, 50, 63, 106, 126], [71, 18, 11, 21, 41, 79]], 120]
    setup_costs(params)
    avg = get_average(params)
    print(avg)

    main(dataset_filename_, output_data_filename_, min_dist=avg)
