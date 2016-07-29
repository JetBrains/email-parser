from edit_distance.token import Token


def get_ids(tokens, token_type):
    day_ids = []
    for i in range(len(tokens)):
        if tokens[i].type_tuple[0] == token_type:
            day_ids.append(i)
    return day_ids


def define_date_related_tokens(tokens):
    day_ids = get_ids(tokens, "DAY")
    year_ids = get_ids(tokens, "YEAR")

    def try_to_update_token_type(token_id):
        if tokens[token_id].type_tuple[0] == "UNDEFINED":
            tokens[token_id].set_type("DATE_RELATED")
            return True
        return False

    while len(day_ids) > 0 and len(year_ids) > 0:
        day_id = day_ids[0]
        year_id = year_ids[0]
        min_id = min(day_ids[0], year_ids[0])
        max_id = max(day_ids[0], year_ids[0])
        if max_id - min_id - 1 > 2:
            if day_id < year_id:
                del day_ids[0]
            else:
                del year_ids[0]
            continue
        else:
            if max_id - min_id - 1 == 2:
                try_to_update_token_type(min_id + 1)
                try_to_update_token_type(min_id + 2)
            elif max_id - min_id - 1 == 1:
                try_to_update_token_type(min_id + 1)
                if min_id > 0:
                    try_to_update_token_type(min_id - 1)
            elif max_id - min_id - 1 == 0:
                if min_id > 0:
                    try_to_update_token_type(min_id - 1)
                if min_id - 1 > 0:
                    try_to_update_token_type(min_id - 2)
            del day_ids[0]
            del year_ids[0]
    return tokens


def get_tokens(text):
    tokens = []
    for word in text.split():
        if word != "":
            tokens.append(Token(word))

    tokens = define_date_related_tokens(tokens)

    return tokens


def token_edit_distance(first_header, second_header):
    first_tokens = get_tokens(first_header)
    second_tokens = get_tokens(second_header)

    first_size = len(first_tokens)
    second_size = len(second_tokens)
    prev = [0 for _ in range(first_size + 1)]
    for i in range(1, len(prev)):
        prev[i] = prev[i - 1] + first_tokens[i - 1].get_deletion_cost()
    curr = [0 for _ in range(first_size + 1)]
    for j in range(1, second_size + 1):
        curr[0] = prev[0] + second_tokens[j - 1].get_insertion_cost()
        for i in range(1, first_size + 1):
            ins = prev[i] + second_tokens[j - 1].get_insertion_cost()
            deletion = curr[i - 1] + first_tokens[i - 1].get_deletion_cost()
            repl = prev[i - 1] + first_tokens[i - 1].get_difference(second_tokens[j - 1])
            curr[i] = min(ins, deletion, repl)
        curr, prev = prev, curr
    return prev[first_size]
