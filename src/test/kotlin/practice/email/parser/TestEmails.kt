package practice.email.parser

import java.util.*
import javax.mail.internet.MailDateFormat

object TestEmails {
    private val mailDateFormat = MailDateFormat();

    private fun getAddressesList(vararg addresses: String): ArrayList<String> {
        val adrses: ArrayList<String> =  ArrayList()
        addresses.forEach { adrses.add(it) }
        return adrses
    }

    val yam_tb_eng_rus = Email(
            mailDateFormat.parse("""Mon, 14 Mar 2016 17:18:00 +0300"""),
            getAddressesList("""Павел Жук <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """some english и русский""",
            Content(
"""Тут находится a little mix from different languages.
""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val gmail_tb_email_parser_task2_5_quotes = Email(
            mailDateFormat.parse("""Wed, 2 Mar 2016 11:20:17 +0300"""),
            getAddressesList("""Mariya Davydova <mariya.davydova@jetbrains.com>"""),
            getAddressesList("""Павел Жук <ppzhuk@gmail.com>"""),
            """Re: email-parser task#2""",
            Content(
"""Привет,

В принципе, я более-менее живая и в офисе. Если не напишу до 15-00, 
встречаемся в 17-00 в офисе.

Скажи девочкам на ресепшене, что ты ко мне на практику, они либо 
пропустят, либо позовут меня. Я сижу в 508 (кабинет прямо напротив 
лифтовой площадки).

On 03/02/2016 12:10 AM, Павел Жук wrote:
> Почта с телефона доступна почти всегда, желательно где-то до 15:00 -
> 15:30 меня предупредить. Если завтра не придет письма, считать что
> встреча состоится?
> Желаю поправиться.
>
> 1 марта 2016 г., 19:15 пользователь Mariya Davydova
> <mariya.davydova@jetbrains.com> написал:
>> Паша,
>>
>> Я приболела. Я очень надеюсь, что завтра смогу выйти, но не уверена. Как часто ты читаешь почту? Иными словами, когда крайний срок, чтобы предупредить тебя, если я все-таки разболеюсь?
>>
>> Sent from my iPad
>>
>>> On Mar 1, 2016, at 13:35, Павел Жук <ppzhuk@gmail.com> wrote:
>>>
>>> Нотификации получил, сегодня вечером займусь исправлением.
>>>
>>> 29 февраля 2016 г., 17:56 пользователь Mariya Davydova
>>> <mariya.davydova@jetbrains.com> написал:
>>>> Привет,
>>>>
>>>> Сделала глобальное ревью на весь код, оставила кучу комментов (если я
>>>> правильно понимаю гитхаб, он уже должен забросать тебя нотификациями на эту
>>>> тему). Если нотификаций вдруг нет, то просто почитай коммиты, комментарии
>>>> аттачатся именно к ним.
>>>>
>>>> В целом хорошо, но много мелочей, на которые стоит обратить внимание. Мне бы
>>>> хотелось, чтобы ты пофиксил указанные замечания.
>>>>
>>>>> On 02/27/2016 03:29 AM, Павел Жук wrote:
>>>>>
>>>>> Привет.
>>>>>
>>>>> Я сделал вторую задачу, все в репозитории, посмотри, пожалуйста.
>>>>> Подойдет ли такой вариант запуска gradle? (то что запуск задачи run и
>>>>> передача числа происходят не вместе).
>>>>>
>>>>> Id задачи указан не во всех коммитах (поздно вспомнил), но там они все
>>>>> относятся ко второй задаче.
>>>>>
>>>> --
>>>> Best regards,
>>>> Mariya Davydova
>>>> Software Developer
>>>> JetBrains
>>>> http://www.jetbrains.com
>>>> The Drive to Develop
>>>>
>>>
>>>
>>> --
>>> Жук Павел.
>
>

-- 
Best regards,
Mariya Davydova
Software Developer
JetBrains
http://www.jetbrains.com
The Drive to Develop

""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val wc_test = Email(
            mailDateFormat.parse("""Mon, 14 Mar 2016 16:41:54 +0300"""),
            getAddressesList("""pav9147@yandex.ru"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """Тест из веб-клиента""",
            Content(
"""тест
""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val gmailMariya = Email(
            mailDateFormat.parse("""Sat, 27 Feb 2016 03:29:25 +0300"""),
            getAddressesList("""Павел Жук <ppzhuk@gmail.com>"""),
            getAddressesList("""Mariya Davydova <mariya.davydova@jetbrains.com>"""),
            """email-parser task#2""",
            Content(
"""Привет.

Я сделал вторую задачу, все в репозитории, посмотри, пожалуйста.
Подойдет ли такой вариант запуска gradle? (то что запуск задачи run и
передача числа происходят не вместе).

Id задачи указан не во всех коммитах (поздно вспомнил), но там они все
относятся ко второй задаче.

-- 
Жук Павел.
""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val e6_2_receive_q2 = Email(
            mailDateFormat.parse("""Sun, 13 Mar 2016 01:02:14 +0300"""),
            getAddressesList("""pav9147@yandex.ru"""),
            getAddressesList("""Паша Жук <jojograf@yandex.ru>"""),
            """Re: тема письма""",
            Content(
"""цитата 2

13.03.2016, 01:01, "Паша Жук" <jojograf@yandex.ru>:
> цитата 1
>
> 13.03.2016, 01:01, "pav9147@yandex.ru" <pav9147@yandex.ru>:
>>  без цитаты""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val fwd_gmail_tb = Email(
            mailDateFormat.parse("""Thu, 17 Mar 2016 14:12:06 +0300"""),
            getAddressesList("""Павел Жук <ppzhuk@gmail.com>"""),
            getAddressesList("""thebostor@gmail.com"""),
            """Fwd: Re: Практика""",
            Content(
                    """переслал из TB


-------- Перенаправленное сообщение --------
Тема: 	Re: Практика
Дата: 	Mon, 14 Mar 2016 14:27:20 +0300
От: 	Павел Жук <ppzhuk@gmail.com>
Кому: 	Mariya Davydova <mariya.davydova@jetbrains.com>



Привет!

Пока помощь не требуется. Я планирую сегодня, завтра усиленно
поработать над проектом. В зависимости от результатов завтра отпишусь,
стоит ли встречаться в среду или нет.

14 марта 2016 г., 12:53 пользователь Mariya Davydova
<mariya.davydova@jetbrains.com> написал:
> Привет!
>
> Я вижу, что ты коммитишь, процесс идёт :) Нужна ли сейчас какая-то помощь?
> Будет ли смысл встречаться в среду?
>
> --
> Best regards,
> Mariya Davydova
> Software Developer
> JetBrains
> http://www.jetbrains.com
> The Drive to Develop
>



-- 
Жук Павел.



""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val fwd_yam_tb = Email(
            mailDateFormat.parse("""Thu, 17 Mar 2016 14:07:01 +0300"""),
            getAddressesList("""Павел Жук <jojograf@yandex.ru>"""),
            getAddressesList("""pav9147@yandex.ru"""),
            """Fwd: тема письма""",
            Content(
                    """


-------- Перенаправленное сообщение --------
Тема: 	тема письма
Дата: 	Sun, 13 Mar 2016 01:01:33 +0300
От: 	pav9147@yandex.ru
Кому: 	jojograf@yandex.ru



без цитаты



""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val from_outlook_eng = Email(
            mailDateFormat.parse("""Tue, 15 Mar 2016 00:50:13 +0300"""),
            getAddressesList("""Yandex Mail <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """theme""",
            Content(
                    """Just an email

""".replace("\n", "\r\n")
                    , null, null
            )
    )

    val from_outlook_ru = Email(
            mailDateFormat.parse("""Tue, 15 Mar 2016 00:24:04 +0300"""),
            getAddressesList("""Yandex Mail <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """Проверяем""",
            Content(
                    """Проверочное письсмо


""".replace("\n", "\r\n")
                    , null, null
            )
    )

}