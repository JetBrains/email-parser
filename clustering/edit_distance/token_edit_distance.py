from edit_distance.token import Token


def get_tokens(text):
    tokens = []
    for word in text.split():
        if word != "":
            tokens.append(Token(word))
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
            deletion = curr[i-1] + first_tokens[i - 1].get_deletion_cost()
            repl = prev[i-1] + first_tokens[i-1].get_difference(second_tokens[j - 1])
            curr[i] = min(ins, deletion, repl)
        curr, prev = prev, curr
    return prev[first_size]
