from cluster.token import Token, check, attribute_reg_ex


def get_ids(tokens, token_type):
    day_ids = []
    for i in range(len(tokens)):
        if tokens[i].type_name == token_type:
            day_ids.append(i)
    return day_ids


def define_date_related_tokens(tokens):
    day_ids = get_ids(tokens, "DAY")
    year_ids = get_ids(tokens, "YEAR")

    def try_to_update_token_type(token_id):
        if tokens[token_id].type_name == "UNDEFINED":
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


def unite_undefined_tokens(tokens):
    i = 0
    united_tokens = []
    while i < len(tokens):
        while i < len(tokens) and tokens[i].type_name != "UNDEFINED":
            united_tokens.append(tokens[i])
            i += 1
        if i >= len(tokens):
            break
        aggregated_token = tokens[i]
        aggregated_token.text += " "
        i += 1
        while i < len(tokens) - 1 and tokens[i].type_name == "UNDEFINED":
            aggregated_token.text += tokens[i].text + " "
            i += 1

        if i == len(tokens) - 1 and tokens[i].type_name == "UNDEFINED":
            if not tokens[i].has_last_colon:
                aggregated_token.text += tokens[i].text
                united_tokens.append(aggregated_token)
            else:
                aggregated_token.text = aggregated_token.text.strip(" ")
                united_tokens.append(aggregated_token)
                united_tokens.append(tokens[i])
            i += 1
        else:
            aggregated_token.text = aggregated_token.text.strip(" ")
            united_tokens.append(aggregated_token)

    return united_tokens


def split_tokens(text):
    tokens = []
    for word in text.split():
        if word != "":
            tokens.append(Token(word))
    return tokens


def set_attributes(tokens):
    if check(attribute_reg_ex["LAST_COLON"], tokens[-1].text):
        tokens[-1].has_last_colon = True


def get_tokens(text):
    tokens = split_tokens(text)
    tokens = define_date_related_tokens(tokens)

    set_attributes(tokens)

    tokens = unite_undefined_tokens(tokens)
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
            repl = prev[i - 1] + first_tokens[i - 1].get_difference(
                second_tokens[j - 1])
            curr[i] = min(ins, deletion, repl)
        curr, prev = prev, curr
    return prev[first_size]


def get_distance_matrix(headers, verbose=False, step=10):
    if verbose:
        print("Evaluating distance matrix...")
    size = len(headers)
    distances = [[0 for _ in range(size)] for _ in range(size)]
    for i in range(1, size):
        for j in range(i):
            distances[i][j] = token_edit_distance(headers[i], headers[j])
            distances[j][i] = distances[i][j]
        if verbose and i % step == 0:
            print("Distances for %d/%d computed." % (i, size))
    if verbose:
        print("Done")
    return distances
