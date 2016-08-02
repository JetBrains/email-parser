import re

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


class Token:
    # --- Customizable values ---

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
    Usage:
        type_min_id = min(token1.type_id, token2.type_id)
        type_max_id = max(token1.type_id, token2.type_id)
        Token.REPLACEMENT_COST[type_max_id][type_min_id]
    '''
    REPLACEMENT_COST = (
        (0,),
        (15, 0),
        (15, 15, 0),
        (15, 15, 15, 0),
        (35, 35, 35, 35, 0),
        (10, 10, 10, 10, 10, 0),
        (50, 50, 50, 50, 50, 50, 0)
    )

    LAST_COLON_INEQUALITY_COST = 35

    # ----------------------------

    def __get_token_type_tuple(self):
        for type, index in token_type.items():
            if type != "UNDEFINED" and type != "DATE_RELATED" and check(token_reg_ex[type], self.text):
                return type, index
        return "UNDEFINED", 0

    def __init__(self, text):
        self.text = text
        type_name, type_id = self.__get_token_type_tuple()
        self.type_name = type_name
        self.type_id = type_id
        self.has_last_colon = False

    def __ne__(self, other):
        if not isinstance(other, Token):
            return NotImplemented
        elif self is other:
            return True
        else:
            if self.type_id != other.type_id:
                return False
            if self.has_last_colon != other.has_last_colon:
                return False
            return True

    def get_insertion_cost(self):
        return self.INSERTION_COST[self.type_name]

    def get_deletion_cost(self):
        return self.get_insertion_cost()

    def last_colon_difference(self, other):
        if self.has_last_colon != other.has_last_colon:
            return Token.LAST_COLON_INEQUALITY_COST
        else:
            return 0

    def get_difference(self, other):
        difference = 0

        if self.type_name != other.type_name:
            type_min_id = min(self.type_id, other.type_id)
            type_max_id = max(self.type_id, other.type_id)
            difference += self.REPLACEMENT_COST[type_max_id][type_min_id]

        difference += self.last_colon_difference(other)

        return difference

    def __str__(self):
        return self.text + "(" + self.type_name + ") last_colon = " + str(self.has_last_colon)

    def set_type(self, new_type_name):
        if new_type_name not in token_type:
            raise Exception("Illegal token type.")
        self.type_name = new_type_name
        self.type_id = token_type[new_type_name]
