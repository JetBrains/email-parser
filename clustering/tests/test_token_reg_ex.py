import re
import unittest

from edit_distance import token


class DateTokenTests(unittest.TestCase):
    def setUp(self):
        self.text = "2016,"

    def test1(self):
        result = re.findall(token.token_reg_ex["DIGITS"], self.text)
        self.assertEqual(result[0], self.text)

    def test2(self):
        result = re.findall(".*,", self.text)
        self.assertEqual(result[0], self.text)


if __name__ == '__main__':
    unittest.main()

