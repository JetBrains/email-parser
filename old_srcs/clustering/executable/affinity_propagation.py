import sys
import os

sys.path.append(os.getcwd())

import time
import numpy as np
from sklearn.cluster import AffinityPropagation
from sklearn import metrics
from cluster.prepare_data import get_headers_pairs_list, get_labels, \
    get_affinity_matrix, read_dist_matrix, \
    write_clusterized_data, print_metrics, setup_costs
from cluster.token_edit_distance import get_distance_matrix
from cluster.visualization import visualize


def clustering(headers, distance_matrix_filename=None):
    if distance_matrix_filename is None:
        dist_matrix, max_dist = get_distance_matrix(headers)
    else:
        dist_matrix, max_dist = read_dist_matrix(distance_matrix_filename)

    affinity_matr = get_affinity_matrix(dist_matrix, max_affinity=max_dist)

    af = AffinityPropagation(affinity="precomputed", copy=True).fit(
        affinity_matr)

    return metrics.silhouette_score(np.asmatrix(dist_matrix), af.labels_,
                                    metric='precomputed')


def main(dataset_filename, output_data_filename,
         distance_matrix_filename=None, display=False):
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

    # pd.write_dist_matrix(dist_matrix, max_dist,
    #                      r"C:\Users\pavel.zhuk\IdeaProjects\email-"
    #                      r"parser\clustering\data\training_set_dist_matr.txt"
    #                      r"", verbose=True)

    affinity_matr = get_affinity_matrix(dist_matrix, verbose=True,
                                        max_affinity=max_dist)
    print("Clustering...")
    af = AffinityPropagation(affinity="precomputed", verbose=True,
                             copy=True).fit(affinity_matr)
    print("Done.")

    cluster_centers_indices = af.cluster_centers_indices_
    n_clusters_ = len(cluster_centers_indices)
    labels = af.labels_

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
        visualize(dist_matrix, labels, cluster_centers_indices,
                  show_cluster_sizes=True)


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

    setup_costs([[108, 116, 112, 76, 100, 147, 158], [[66], [36, 143], [158, 41, 63], [86, 56, 61, 12], [136, 10, 10, 190, 119], [146, 10, 52, 64, 75, 122]], 157])

    main(dataset_filename_, output_data_filename_)
