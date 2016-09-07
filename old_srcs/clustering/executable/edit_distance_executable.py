import sys
import os

sys.path.append(os.getcwd())

from cluster.token_edit_distance import token_edit_distance

headers = (
    "From: \"XXX YYY (Someone Important)\" <e@ma.il> Date: 04/23/2015 9:23 PM "
    "(GMT+08:00) To: zzz xxx <xxx@xx.ss.net> Subject: [text text] Re: [ "
    "text - text text ] text text",

    "From: \"zzz xxx (Someone Important)\" <mail@mail.com> Date: 10/15/2015"
    "3:58 PM (GMT-06:00) To: \"xxx ccc\" <aaa@ss.com> Subject: [text text] Re:"
    "text text text text text text text",

    "2014-12-29 18:59 GMT+03:00 VVVVV <mail@mail.com>:",

    "2015-09-17 17:36 GMT+03:00 xxx yyy (Someone Important) "
    "<mail.mail@mail.com>:",

    "Le 16/03/2016 13:21 XxX yYy a écrit:",

    "W dniu 30.06.2014 15:22 yYy XxX pisze:",

    '''On Feb 6 2015 5:47 PM "FFF GGG" <e@mail.com> wrote:''',

    "On Jan 20 2015 at 18:47 XXX ZZZ <e@mail.com> wrote:",

    "text text text text text www.example.com/page text text text text text "
    "text 11/25/2014 07:31 EST:"
)

distances = []

for i, a in enumerate(headers):
    for j, b in enumerate(headers):
        if j > i:
            distances.append(
                (a, b, token_edit_distance(a, b))
            )

distances.sort(key=lambda x: x[2])

for triple in distances:
    print("a = " + triple[0])
    print("b = " + triple[1])
    print("Editing distance = " + str(triple[2]))
    print()
