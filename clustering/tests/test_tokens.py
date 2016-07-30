import unittest

from edit_distance.token_edit_distance import get_tokens, split_tokens, define_date_related_tokens, set_attributes, \
    unite_undefined_tokens

# todo add before method??

class TokenTests(unittest.TestCase):

    def test_token_types(self):
        text = "On Mon Jul 1 2016 at 2:08 PM Unknown Person (some body) <xxx.yyy@zzz.com> wrote:"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

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
        self.assertTrue(tokens[13].has_last_colon)

    def test_token_types_2(self):
        text = "*From:* Unknown Person (some body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

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
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

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

        self.assertFalse(tokens[8].has_last_colon)

    def test_double_date_2(self):
        text = "*From:* Unknown 19 May 2015 Person (some 5 body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

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

        self.assertFalse(tokens[9].has_last_colon)

    def test_one_date_related_token(self):
        text = "16 февр 2016 г в 18:58 Unknown Person (X Y) <email@it.is> написал(а):"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

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
        self.assertTrue(tokens[-1].has_last_colon)

    def test_date_short(self):
        text = "2014-12-29 18:59 GMT+03:00 xxx <e@ma.il>:"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)

        self.assertEqual(len(tokens), 5)
        self.assertEqual(tokens[0].type_tuple[0], "DATE_SHORT")
        self.assertEqual(tokens[1].type_tuple[0], "TIME")
        self.assertEqual(tokens[2].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[3].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[4].type_tuple[0], "EMAIL")
        self.assertTrue(tokens[-1].has_last_colon)

    def test_undefined_tokens_unification(self):
        text = "2014-12-29 18:59 GMT+03:00 xxx <e@ma.il>:"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)
        tokens = unite_undefined_tokens(tokens)

        self.assertEqual(len(tokens), 4)
        self.assertEqual(tokens[2].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[2].text, "GMT+03:00 xxx")
        self.assertTrue(tokens[-1].has_last_colon)

    def test_undefined_tokens_unification_2(self):
        text = "*From:* Unknown Person (some body)) [mailto: xxx.ccc@aaa.ss] *Sent:* 19 May 2015 03:29 PM"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)
        tokens = unite_undefined_tokens(tokens)

        self.assertEqual(len(tokens), 8)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[0].text, "*From:* Unknown Person (some body)) [mailto:")

    def test_undefined_tokens_unification_3(self):
        text = "*From:* Unknown 19 May 2015 Person (some body)) [mailto: xxx.ccc@aaa.ss] xxx vvv eee *Sent:* 19 May 2015 03:29 PM zz"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)
        tokens = unite_undefined_tokens(tokens)

        self.assertEqual(len(tokens), 14)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[5].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[7].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[13].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[0].text, "*From:*")
        self.assertEqual(tokens[5].text, "Person (some body)) [mailto:")
        self.assertEqual(tokens[7].text, "xxx vvv eee")
        self.assertEqual(tokens[13].text, "PM zz")

    def test_undefined_tokens_unification_4(self):
        text = "On 04/15/2016 05:08 PM Павел Жук wrote:"
        tokens = split_tokens(text)
        tokens = define_date_related_tokens(tokens)
        set_attributes(tokens)
        tokens = unite_undefined_tokens(tokens)

        self.assertEqual(len(tokens), 5)
        self.assertEqual(tokens[0].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[1].type_tuple[0], "DATE_SHORT")
        self.assertEqual(tokens[2].type_tuple[0], "TIME")
        self.assertEqual(tokens[3].type_tuple[0], "UNDEFINED")
        self.assertEqual(tokens[4].type_tuple[0], "UNDEFINED")
        self.assertTrue(tokens[4].has_last_colon)
        self.assertEqual(tokens[3].text, "PM Павел Жук")

if __name__ == '__main__':
    unittest.main()

