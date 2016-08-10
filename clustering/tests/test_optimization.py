import unittest
import re

from cluster.token import Token
from optimization.ap_optim import bound_costs, MAX_COST, MIN_COST


class OptimizationTests(unittest.TestCase):
    def test_bound_costs(self):
        c = [
            [10, 15, -30, 30, 240, 10, 50],
            Token.REPLACEMENT_COST,
            Token.LAST_COLON_INEQUALITY_COST
        ]
        c[2] = 555
        c[1][2][1] = -1
        c[1][3][2] = 1000
        v = bound_costs(c, [])
        self.assertEqual(MIN_COST, v[0][2])
        self.assertEqual(MAX_COST, v[0][4])
        self.assertEqual(MIN_COST, v[1][2][1])
        self.assertEqual(MAX_COST, v[1][3][2])
        self.assertEqual(MAX_COST, v[2])


if __name__ == '__main__':
    unittest.main()
