import unittest
import re

from cluster.token import Token
from cluster.token import token_type
from cluster.token import attribute_reg_ex


class TokenRegExTests(unittest.TestCase):
    def test_year_token(self):
        tkn = Token("2016,")
        self.assertEqual(token_type["YEAR"], tkn.type_id)

    def test_email_token(self):
        tkn = Token("<mdfg.dfgg@cvbvc.com>")
        self.assertEqual(token_type["EMAIL"], tkn.type_id)

    def test_email_token_2(self):
        tkn = Token("ppzh@mail.com")
        self.assertEqual(token_type["EMAIL"], tkn.type_id)

    def test_undefined_token(self):
        tkn = Token("@ppzhuk")
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)

    def test_time_token(self):
        tkn = Token("23:59")
        self.assertEqual(token_type["TIME"], tkn.type_id)
        self.assertFalse(tkn.has_last_colon)

    def test_time_token_2(self):
        tkn = Token("23:59:00")
        self.assertEqual(token_type["TIME"], tkn.type_id)
        self.assertFalse(tkn.has_last_colon)

    def test_time_token_3(self):
        tkn = Token("23:59:")
        res = re.match(attribute_reg_ex["LAST_COLON"], tkn.text)
        self.assertEqual(token_type["TIME"], tkn.type_id)
        self.assertTrue(res is not None)
        self.assertTrue(res.group() == tkn.text)

    def test_time_token_4(self):
        tkn = Token("23:59:55,")
        self.assertEqual(token_type["TIME"], tkn.type_id)
        self.assertFalse(tkn.has_last_colon)

    def test_time_incorrect_token(self):
        tkn = Token("+23:59")
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)
        self.assertFalse(tkn.has_last_colon)

    def test_meridiem_token(self):
        tkn = Token("PM")
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)

    def test_meridiem_token_2(self):
        tkn = Token("a.m.,")
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)

    def test_date_token(self):
        tkn = Token("2006.05.04")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_2(self):
        tkn = Token("2006-5-4")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_3(self):
        tkn = Token("2006/5/4")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_4(self):
        tkn = Token("4.5.2006")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_5(self):
        tkn = Token("4-5-2006")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_6(self):
        tkn = Token("4/5/2006")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_7(self):
        tkn = Token("04.05.2006")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_8(self):
        tkn = Token("04-05-2006")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_date_token_9(self):
        tkn = Token("2006/04/05,")
        self.assertEqual(token_type["DATE_SHORT"], tkn.type_id)

    def test_non_letter_or_digit(self):
        tkn = Token(r'''}{[]\|\|?/.,><';:"~`''')
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)
        self.assertFalse(tkn.has_last_colon)

    def test_angle_bracket(self):
        tkn = Token(">:")
        res = re.match(attribute_reg_ex["LAST_COLON"], tkn.text)
        self.assertEqual(token_type["UNDEFINED"], tkn.type_id)
        self.assertTrue(res is not None)
        self.assertTrue(res.group() == tkn.text)


if __name__ == '__main__':
    unittest.main()
