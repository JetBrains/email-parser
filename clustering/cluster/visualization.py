def visualize(dist_matrix, labels, n_clusters, show_cluster_sizes=False):
    import matplotlib.pyplot as plt
    from sklearn import manifold

    mds = manifold.MDS(n_components=2, dissimilarity="precomputed", random_state=13)
    results = mds.fit(dist_matrix)

    coords = results.embedding_

    plt.scatter(
        coords[:, 0], coords[:, 1], marker='o', c=labels, s=150, edgecolors="face", alpha=0.7
    )

    if not show_cluster_sizes:
        plt.show()
        return

    cluster_size = [len(list(filter(lambda x: x == i, labels))) for i in range(n_clusters)]
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