import re
import math

token_reg_ex = {
    "DAY": re.compile("^\\d{1,2}[,\\.]?:?$"),
    "YEAR": re.compile("^[12]\\d{3}[,\\.]?:?$"),
    "DATE_SHORT": re.compile("^(([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" +
                             "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))[,\\.]?:?$"),
    "TIME": re.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?[,\\.]?:?$"),
    "EMAIL": re.compile("\\S+@\\S+")
}

attribute_reg_ex = {
    "LAST_COLON": re.compile(".*:$")
}

token_type = {
    "UNDEFINED": 0,
    "DATE_RELATED": 1,
    "DAY": 2,
    "YEAR": 3,
    "DATE_SHORT": 4,
    "TIME": 5,
    "EMAIL": 6
}


def check(regex, text):
    res = re.match(regex, text)
    if res is not None:
        return res.group() == text
    return False

# todo replace type_tuple with 2 fields or add get methods

class Token:

    INSERTION_COST = {
        "UNDEFINED": 10,
        "DATE_RELATED": 15,
        "DAY": 30,
        "YEAR": 30,
        "DATE_SHORT": 40,
        "TIME": 10,
        "EMAIL": 50
    }

    '''
    The cost of replacement the first token by the second.
    Usage: REPLACEMENT_COST[token1.type][token2.type]
    '''
    REPLACEMENT_COST = (
        (0, 15, 15, 15, 35, 10, 50),
        (15, 0, 15, 15, 35, 10, 50),
        (15, 15, 0, 15, 35, 10, 50),
        (15, 15, 15, 0, 35, 10, 50),
        (35, 35, 35, 35, 0, 10, 50),
        (10, 10, 10, 10, 10, 0, 50),
        (50, 50, 50, 50, 50, 50,  0)
    )

    LAST_COLON_INEQUALITY_COST = 35

    def __get_token_type_tuple(self):
        for type, index in token_type.items():
            if type != "UNDEFINED" and type != "DATE_RELATED" and check(token_reg_ex[type], self.text):
                return type, index
        return "UNDEFINED", 0

    def __init__(self, text):
        self.text = text
        self.type_tuple = self.__get_token_type_tuple()
        self.has_last_colon = False

    def __ne__(self, other):
        if not isinstance(other, Token):
            return NotImplemented
        elif self is other:
            return True
        else:
            if self.type_tuple != other.type_tuple:
                return False
            if self.has_last_colon != other.has_last_colon:
                return False
            return True

    def get_insertion_cost(self):
        return self.INSERTION_COST[self.type_tuple[0]]

    def get_deletion_cost(self):
        return self.get_insertion_cost()

    def last_colon_difference(self, other):
        if self.has_last_colon != other.has_last_colon:
            return Token.LAST_COLON_INEQUALITY_COST
        else:
            return 0

    def get_difference(self, other):
        difference = 0

        if self.type_tuple[0] != other.type_tuple[0]:
            difference += self.REPLACEMENT_COST[self.type_tuple[1]][other.type_tuple[1]]

        difference += self.last_colon_difference(other)

        return difference

    def __str__(self):
        return self.text + "(" + self.type_tuple[0] + ") last_colon = " + str(self.has_last_colon)

    def set_type(self, new_type):
        if new_type not in token_type:
            raise Exception("Illegal token type.")
        new_type_idx = token_type[new_type]
        self.type_tuple = (new_type, new_type_idx)
