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


def clustering(headers, eps, distance_matrix_filename=None):
    if distance_matrix_filename is None:
        dist_matrix, _ = get_distance_matrix(headers)
    else:
        dist_matrix, _ = read_dist_matrix(distance_matrix_filename)

    dbscan = DBSCAN(eps=eps, min_samples=2, metric="precomputed").fit(
        dist_matrix)

    return metrics.silhouette_score(np.asmatrix(dist_matrix), dbscan.labels_,
                                    metric='precomputed')


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

    # setup_costs([[104, 143, 153, 8, 175, 0, 0],
    #              [[44], [78, 0], [34, 173, 191], [33, 188, 173, 0],
    #               [0, 174, 115, 200, 183], [119, 162, 130, 19, 2, 199]], 150])

    main(dataset_filename_, output_data_filename_,
         eps=10)
