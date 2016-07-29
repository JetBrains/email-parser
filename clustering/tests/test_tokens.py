import unittest

from edit_distance.token_edit_distance import get_tokens


class TokenRegExTests(unittest.TestCase):

    def test_token_types(self):
        text = "On Mon Jul 1 2016 at 2:08 PM Unknown Person (some body) <xxx.yyy@zzz.com> wrote:"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 14)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[1].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[2].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[3].type_tuple[0], "DAY")
        self.assertEqual(tokens[4].type_tuple[0], "YEAR")
        self.assertEqual(tokens[5].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[6].type_tuple[0], "TIME")
        self.assertEqual(tokens[7].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[12].type_tuple[0], "EMAIL")
        self.assertEqual(tokens[13].type_tuple[0], "UNDEFINED")

    def test_token_types_2(self):
        text = "*From:* Unknown Person (some body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 13)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[5].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[6].type_tuple[0], "EMAIL")
        self.assertEqual(tokens[7].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[8].type_tuple[0], "DAY")
        self.assertEqual(tokens[9].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[10].type_tuple[0], "YEAR")
        self.assertEqual(tokens[11].type_tuple[0], "TIME")
        self.assertEqual(tokens[12].type_tuple[0], "UNDEFINED")

    def test_double_date(self):
        text = "*From:* Unknown 19 May 2015 Person (some body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 16)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[1].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[2].type_tuple[0], "DAY")
        self.assertEqual(tokens[3].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[4].type_tuple[0], "YEAR")
        self.assertEqual(tokens[5].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[6].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[7].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[8].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[9].type_tuple[0], "EMAIL")
        self.assertEqual(tokens[10].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[11].type_tuple[0], "DAY")
        self.assertEqual(tokens[12].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[13].type_tuple[0], "YEAR")
        self.assertEqual(tokens[14].type_tuple[0], "TIME")
        self.assertEqual(tokens[15].type_tuple[0], "UNDEFINED")

    def test_double_date_2(self):
        text = "*From:* Unknown 19 May 2015 Person (some 5 body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 17)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[1].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[2].type_tuple[0], "DAY")
        self.assertEqual(tokens[3].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[4].type_tuple[0], "YEAR")
        self.assertEqual(tokens[5].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[6].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[7].type_tuple[0], "DAY")
        self.assertEqual(tokens[8].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[9].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[10].type_tuple[0], "EMAIL")
        self.assertEqual(tokens[11].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[12].type_tuple[0], "DAY")
        self.assertEqual(tokens[13].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[14].type_tuple[0], "YEAR")
        self.assertEqual(tokens[15].type_tuple[0], "TIME")
        self.assertEqual(tokens[16].type_tuple[0], "UNDEFINED")

    def test_one_date_related_token(self):
        text = "16 февр 2016 г в 18:58 Unknown Person (X Y) <email@it.is> написал(а):"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 12)
        self.assertEqual(tokens[0].type_tuple[0], "DAY")
        self.assertEqual(tokens[1].type_tuple[0], "DATE_RELATED")
        self.assertEqual(tokens[2].type_tuple[0], "YEAR")
        self.assertEqual(tokens[3].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[4].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[5].type_tuple[0], "TIME")
        self.assertEqual(tokens[6].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[10].type_tuple[0], "EMAIL")
        self.assertEqual(tokens[11].type_tuple[0], "UNDEFINED")

    def test_date_short(self):
        text = "2014-12-29 18:59 GMT+03:00 xxx <e@ma.il>:"
        tokens = get_tokens(text)
        self.assertEqual(len(tokens), 5)
        self.assertEqual(tokens[0].type_tuple[0], "DATE_SHORT")
        self.assertEqual(tokens[1].type_tuple[0], "TIME")
        self.assertEqual(tokens[2].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[3].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[4].type_tuple[0], "EMAIL")

if __name__ == '__main__':
    unittest.main()

