import re
import numpy as np

TOKEN_END = "[,\\.]?:?"

token_reg_ex = {
    "DIGITS": re.compile("\\d+" + TOKEN_END),
    "DATE": re.compile("(([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" +
                       "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))" + TOKEN_END),
    "TIME": re.compile("([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?" + TOKEN_END),
    "MERIDIEM": re.compile("(A|a|P|p)\\.?(M|m)\\.?" + TOKEN_END),
    "EMAIL": re.compile("\\S+@\\S+")
}

attribute_reg_ex = {
    "ANGLE_BRACKETS": re.compile("<.*>" + TOKEN_END),
    "LAST_COMMA": re.compile(".*,"),
    "LAST_COLON": re.compile(".*:"),
    "HAS_AT": re.compile(".*@.*"),
    "NON_ALPHABETIC": re.compile("([0-9]|[!-/]|[:-@]|[\\[-`]|[{-~])+"),
    "NON_ALPHABETIC_OR_DIGIT": re.compile("([!-/]|[:-@]|[\\[-`]|[{-~])+")
}

TOKEN_TYPE_UNDEFINED = -1

token_type = {
    "DIGITS": 0,
    "DATE": 1,
    "TIME": 2,
    "MERIDIEM": 3,
    "EMAIL": 4
}


def check(regex, text):
    return len(re.findall(regex, text)) > 0


class Attributes:
    def __init__(self, outer_token_obj):
        self.lastComma = check(attribute_reg_ex["LAST_COMMA"], outer_token_obj.text)
        self.withAngleBrackets = check(attribute_reg_ex["ANGLE_BRACKETS"], outer_token_obj.text)
        self.lastColon = check(attribute_reg_ex["LAST_COLON"], outer_token_obj.text)
        self.hasAtSymbol = check(attribute_reg_ex["HAS_AT"], outer_token_obj.text)
        self.nonLetterOrDigit = check(attribute_reg_ex["NON_ALPHABETIC_OR_DIGIT"], outer_token_obj.text)
        self.nonAlphabetic = check(attribute_reg_ex["NON_ALPHABETIC"], outer_token_obj.text)

    def __ne__(self, other):
        if not isinstance(other, Attributes):
            return NotImplemented
        elif self is other:
            return True
        else:
            if self.lastComma != other.lastComma: return False
            if self.withAngleBrackets != other.withAngleBrackets: return False
            if self.lastColon != other.lastColon: return False
            if self.hasAtSymbol != other.hasAtSymbol: return False
            if self.nonLetterOrDigit != other.nonLetterOrDigit: return False
            if self.nonAlphabetic != other.nonAlphabetic: return False
            return True


class Token:

    INSERTION_COST = {
        "UNDEFINED": 10,
        "DIGITS": 10,
        "DATE": 10,
        "TIME": 10,
        "MERIDIEM": 10,
        "EMAIL": 50
    }

    '''
    The cost of replacement the first token by the second.
    Usage: REPLACEMENT_COST[token1.type][token2.type]
    '''
    REPLACEMENT_COST = (
        (0, 10, 10, 10, 10, 50),
        (10, 0, 10, 10, 10, 50),
        (10, 10, 0, 10, 10, 50),
        (10, 10, 10, 0, 10, 50),
        (10, 10, 10, 10, 0, 50),
        (50, 50, 50, 50, 50, 0),
    )

    DIGITS_INEQUALITY_COST = 10
    ATTRIBUTE_INEQUALITY_COST = 1

    __types = {
        token_type["DIGITS"]: token_reg_ex["DIGITS"],
        token_type["DATE"]: token_reg_ex["DATE"],
        token_type["TIME"]: token_reg_ex["TIME"],
        token_type["MERIDIEM"]: token_reg_ex["MERIDIEM"],
        token_type["EMAIL"]: token_reg_ex["EMAIL"],
    }

    def __get_token_type(self):
        for type in token_type.keys():
            if check(token_reg_ex[type], self.text):
                return type
        return "UNDEFINED"

    def __get_attributes(self):
        return Attributes(self)

    def __init__(self, text):
        self.text = text
        self.token_type = self.__get_token_type()
        self.attrs = self.__get_attributes()

    def __ne__(self, other):
        if not isinstance(other, Token):
            return NotImplemented
        elif self is other:
            return True
        else:
            if self.token_type != other.token_type: return False
            if self.attrs != other.attrs: return False
            return True

    def get_insertion_cost(self):
        return self.INSERTION_COST[self.token_type]

    def get_deletion_cost(self):
        return self.get_deletion_cost()

    # TODO getDifference + fix token_type dict

    def getDifference(self, other):
        difference = 0

        # if (self.token_type != other.token_type):
        #     difference += self.REPLACEMENT_COST[][]
