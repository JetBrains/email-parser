from edit_distance.token_edit_distance import token_edit_distance


headers = (
    "15 апреля 2016 г 20:26 пользователь Mariya Davydova <mmm@jjj.com> написал:",
    "Это в общих чертах 15:22 на деле видимо еще придется учесть несколько частных случаев но в целом g@hh.er кажется что задача поиска заголовка цитаты менее масштабная. Признаки кроме: даты не зависят от языка",
    "El 02/04/16 a las 19:03 Zhuk Pavel escribió:",
    "Воскресенье 13 марта 2016 6:55 +10:00 от Павел Жук <ppp@zzz.com>:",
    "On 04/15/2016 05:08 PM Павел Жук wrote:",
    "29 февраля 2016 г 17:56 пользователь Mariya Davydova <mdrefha@juy.com> написал:",
    "On Mar 1 2016 at 13:35 Павел Жук <pp@g.c> wrote:",
    "On Thu Mar 31 2016 at 4:04 PM Zhuk Pavel <y-k@jjjjj.com> wrote:",
    "Воскресенье 13 марта 2016 7:09 +10:00 от Павел Жук <lkjk@jl.com>:",
)

distances = []

for i, a in enumerate(headers):
    for j, b in enumerate(headers):
        if j > i:
            distances.append((a, b, token_edit_distance(a, b)))

distances.sort(key=lambda x: x[2])

for triple in distances:
    print("a = " + triple[0])
    print("b = " + triple[1])
    print("Editing distance = " + str(triple[2]))
    print()
