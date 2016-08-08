class MST:
    __parent = dict()
    __rank = dict()

    def __make_set(self, vertice):
        self.__parent[vertice] = vertice
        self.__rank[vertice] = 0

    def __find(self, vertice):
        if self.__parent[vertice] != vertice:
            self.__parent[vertice] = self.__find(self.__parent[vertice])
        return self.__parent[vertice]

    def __union(self, vertice1, vertice2):
        root1 = self.__find(vertice1)
        root2 = self.__find(vertice2)
        if root1 != root2:
            if self.__rank[root1] > self.__rank[root2]:
                self.__parent[root2] = root1
            else:
                self.__parent[root1] = root2
                if self.__rank[root1] == self.__rank[root2]:
                    self.__rank[root2] += 1

    def __kruskal(self, graph):
        for vertice in range(graph['vertices_num']):
            self.__make_set(vertice)

        minimum_spanning_tree = set()
        edges = list(graph['edges'])
        edges.sort()
        for edge in edges:
            weight, vertice1, vertice2 = edge
            if self.__find(vertice1) != self.__find(vertice2):
                self.__union(vertice1, vertice2)
                minimum_spanning_tree.add(edge)
        return minimum_spanning_tree

    def get_mst(self, distance_matrix):
        edges = set()
        for i, line in enumerate(distance_matrix):
            for j, e in enumerate(line):
                if j < i:
                    edges.add((distance_matrix[i][j], i, j))
        graph = {
            'vertices_num': len(distance_matrix),
            'edges': edges
        }
        return self.__kruskal(graph)
