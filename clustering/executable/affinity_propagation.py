import sys
import time
import numpy as np
from sklearn.cluster import AffinityPropagation
from sklearn import metrics
from edit_distance.prepare_data import get_headers_pairs_list, get_labels, get_affinity_matrix
from edit_distance.token_edit_distance import get_distance_matrix

start = time.perf_counter()

filename = r"C:\Users\pavel.zhuk\IdeaProjects\email-parser\src\main\resources\datasets\training_sorted_short.txt"

if len(sys.argv) > 1:
    filename = sys.argv[1]

print("Reading headers...")
headers_pairs = get_headers_pairs_list(filename)
print("Forming labels...")
labels_true = get_labels(filename)
headers = list(map(lambda x: x[1], headers_pairs))
print("Evaluating distance matrix...")
dist_matr = get_distance_matrix(headers, verbose=True)
print("Evaluating affinity matrix...")
affinity_matr = get_affinity_matrix(dist_matr)
print("Clustering...")
af = AffinityPropagation(affinity="precomputed", verbose=True, copy=True).fit(affinity_matr)
print("Done.")
print()

cluster_centers_indices = af.cluster_centers_indices_
n_clusters_ = len(cluster_centers_indices)
labels = af.labels_

print('Estimated number of clusters: %d' % n_clusters_)
print("Homogeneity: %0.3f" % metrics.homogeneity_score(labels_true, labels))
print("Completeness: %0.3f" % metrics.completeness_score(labels_true, labels))
print("V-measure: %0.3f" % metrics.v_measure_score(labels_true, labels))
print("Adjusted Rand Index: %0.3f"
      % metrics.adjusted_rand_score(labels_true, labels))
print("Adjusted Mutual Information: %0.3f"
      % metrics.adjusted_mutual_info_score(labels_true, labels))
print("Silhouette Coefficient: %0.3f"
      % metrics.silhouette_score(np.asmatrix(dist_matr), labels, metric='precomputed'))

end = time.perf_counter()
print("\nWorking time: %f" % (end - start))

