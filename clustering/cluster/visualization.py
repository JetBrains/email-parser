import matplotlib.pyplot as plt
from sklearn import manifold


def visualize(dist_matrix, labels, cluster_centers_indices,
              show_cluster_sizes=False):
    n_clusters = len(cluster_centers_indices)
    mds = manifold.MDS(n_components=2, dissimilarity="precomputed",
                       random_state=13)
    results = mds.fit(dist_matrix)

    coords = results.embedding_

    plt.title('Estimated number of clusters: %d' % n_clusters)

    plt.scatter(
        coords[:, 0], coords[:, 1], marker='o', c=labels * 10, s=150,
        edgecolors="face", alpha=0.7
    )

    # Draw lines from elements to cluster centers

    eps = 0.01
    for i in range(len(coords)):
        center_id = cluster_centers_indices[labels[i]]
        center_x = coords[center_id, 0]
        center_y = coords[center_id, 1]
        point_x = coords[i, 0]
        point_y = coords[i, 1]
        if abs(center_x - point_x) < eps and abs(center_y - point_y) < eps:
            continue
        plt.plot([center_x, point_x], [center_y, point_y], 'k', linewidth=1)

    # Draw labels with the cluster size

    if not show_cluster_sizes:
        plt.show()
        return

    cluster_size = [len(list(filter(lambda x: x == i, labels))) for i in
                    range(n_clusters)]
    has_text_label = [False for _ in range(n_clusters)]

    for cluster_id, x, y in zip(labels, coords[:, 0], coords[:, 1]):
        if has_text_label[cluster_id]:
            continue
        has_text_label[cluster_id] = True
        plt.annotate(
            cluster_size[cluster_id],
            xy=(x, y), xytext=(-20, 20),
            textcoords='offset points', ha='right', va='bottom',
            bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
            arrowprops=dict(arrowstyle='->', connectionstyle='arc3,rad=0'))

    plt.show()
