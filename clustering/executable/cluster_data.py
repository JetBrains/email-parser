import sys
import os

sys.path.append(os.getcwd())

import time
import numpy as np
from sklearn.cluster import DBSCAN
from sklearn import metrics
from cluster.prepare_data import get_headers_pairs_list, get_labels, \
    read_dist_matrix, write_clusterized_data, print_metrics, setup_costs
from cluster.token_edit_distance import get_distance_matrix
from cluster.visualization import visualize_dbscan


def main(dataset_filename, output_data_filename,
         distance_matrix_filename=None, eps=10, display=False):
    start = time.perf_counter()

    headers_pairs = get_headers_pairs_list(dataset_filename, verbose=True)
    labels_true = get_labels(dataset_filename, verbose=True)

    if distance_matrix_filename is None:
        dist_matrix, _ = get_distance_matrix(list(map(lambda x: x[1],
                                                      headers_pairs)),
                                             verbose=True)
    else:
        dist_matrix, _ = \
            read_dist_matrix(distance_matrix_filename, verbose=True)

    print("Clustering...")
    dbscan = DBSCAN(eps=eps, min_samples=2, metric="precomputed").fit(
        dist_matrix)
    print("Done.")

    labels = np.copy(dbscan.labels_)

    n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
    clusters_without_noise = n_clusters_

    for i, l in enumerate(labels):
        if l == -1:
            labels[i] = n_clusters_
            n_clusters_ += 1

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

    if display:
        visualize_dbscan(dist_matrix, dbscan.labels_,
                         clusters_without_noise + 1, show_cluster_sizes=True)


def ge_min_cost(costs_):
    (ins_cost, repl_matr, colon_cost) = costs_
    min_cost_ = 200
    for line in repl_matr:
        min_cost_ = min(min_cost_, min(line))

    return min(colon_cost, min_cost_, min(ins_cost))


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

    costs = [[10, 200, 110, 145, 156, 197, 200],
             [[24],
              [95, 198],
              [200, 181, 20],
              [145, 120, 13, 64],
              [200, 154, 196, 112, 10],
              [200, 12, 158, 10, 160, 191]
              ],
             197
             ]
    setup_costs(costs)
    min_cost = ge_min_cost(costs)

    main(dataset_filename_, output_data_filename_, distance_matrix_filename_,
         eps=min_cost, display=True)
