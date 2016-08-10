import unittest
from executable.mst_clustering import get_mst


class MSTTests(unittest.TestCase):
    def test_mst(self):
        dist_matr = [
            [0, 1, 0, 3],
            [1, 0, 4, 2],
            [0, 4, 0, 1],
            [3, 2, 1, 0],
        ]
        real_mst = {(0, 2, 0), (1, 1, 0), (1, 3, 2)}
        calculated_mst = get_mst(dist_matr)
        for t in calculated_mst:
            self.assertTrue(t in real_mst)

    def test_zero_mst(self):
        dist_matr = [
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
        ]
        real_mst = {(0, 2, 0), (0, 1, 0), (0, 3, 0)}
        calculated_mst = get_mst(dist_matr)
        for t in calculated_mst:
            self.assertTrue(t in real_mst)

if __name__ == '__main__':
    unittest.main()
