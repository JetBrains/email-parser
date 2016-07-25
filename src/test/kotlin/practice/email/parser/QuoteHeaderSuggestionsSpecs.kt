package practice.email.parser

import org.jetbrains.spek.api.Spek
import practice.email.parser.QuotesHeaderSuggestions.getQuoteHeaderLine
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuoteHeaderSuggestionsSpecs : Spek() {
    init {
        given("getQuoteHeader() function") {
            on("calling with text with one quote header") {
                val text = """Это в общих чертах, на деле, видимо, еще придется учесть несколько частных
случаев, но в целом, кажется, что задача поиска заголовка цитаты менее
масштабная. Признаки, кроме даты, не зависят от языка.

2. Я нашел пару библиотек для Java по нейросетям и ML
Neuroph <http://neuroph.sourceforge.net/index.html>  - по н.с.
Encog <http://www.heatonresearch.com/encog/> - по ML
поэтому есть возможность оставаться в контексте Java/Kotlin. Python для
всего этого, конечно, подходит лучше, но я с ним не работал, а на его
изучение уйдет какое-то доп. время. Вообще, я сейчас довольно много нового
материала впитываю, это может несколько замедлять процесс, но я стараюсь :)

Скажи, пожалуйста, что ты думаешь по поводу выделения заголовка цитаты по
дате/времени?

15 апреля 2016 г., 20:26 пользователь Mariya Davydova <
mariya.davydova@jetbrains.com> написал:

> Привет,
>
> У меня за последнее время родилось некоторое количество разрозненных
> мыслей, я ими поделюсь, а дальше будем думать :)"""

                val expected = "15 апреля 2016 г., 20:26 пользователь Mariya Davydova < mariya.davydova@jetbrains.com> написал:"
                val actual = getQuoteHeaderLine(text)
                it("should fetch quote header") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with content text with some fake quote suggestions") {
                val text = """Это в общих чертах, на деле, видимо, еще придется учесть несколько частных
случаев, но в целом, g@hh.er кажется, что задача поиска заголовка цитаты менее
масштабная. Признаки, кроме: даты, не зависят от языка.

2. Я нашел пару библиотек 15:22 для Java по нейросетям и ML
Neuroph <http://neuroph.sourceforge.net/index.html>  - по н.с.
Encog <http://www.heatonresearch.com/encog/> - по ML
поэтому есть возможность оставаться в контексте Java/Kotlin. Python для
всего этого, конечно, подходит лучше, но я с ним не работал, а на его
изучение уйдет какое-то доп. время. Вообще, я сейчас довольно много нового
материала впитываю, это может несколько замедлять процесс, но я стараюсь :)

Скажи, пожалуйста, что ты думаешь по поводу выделения заголовка цитаты по
дате/времени?

15 апреля 2016 г., 20:26 пользователь Mariya Davydova <
mariya.davydova@jetbrains.com> написал:

> Привет,
>
> У меня за последнее время родилось некоторое количество разрозненных
> мыслей, я ими поделюсь, а дальше будем думать :)"""

                val expected = "15 апреля 2016 г., 20:26 пользователь Mariya Davydova < mariya.davydova@jetbrains.com> написал:"
                val actual = getQuoteHeaderLine(text)
                it("still should fetch quote header") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with text with concentrated fake quote suggestions") {
                val text = """Это в общих чертах, 15:22 на деле, видимо, еще придется учесть несколько частных
случаев, но в целом, g@hh.er кажется, что задача поиска заголовка цитаты менее
масштабная. Признаки, кроме даты, не: зависят от языка.

2. Я нашел пару библиотек  для Java по нейросетям и ML
Neuroph <http://neuroph.sourceforge.net/index.html>  - по н.с.
Encog <http://www.heatonresearch.com/encog/> - по ML
поэтому есть возможность оставаться в контексте Java/Kotlin. Python для
всего этого, конечно, подходит лучше, но я с ним не работал, а на его
изучение уйдет какое-то доп. время. Вообще, я сейчас довольно много нового
материала впитываю, это может несколько замедлять процесс, но я стараюсь :)

Скажи, пожалуйста, что ты думаешь по поводу выделения заголовка цитаты по
дате/времени?

15 апреля 2016 г., 20:26 пользователь Mariya Davydova <
mariya.davydova@jetbrains.com> написал:

> Привет,
>
> У меня за последнее время родилось некоторое количество разрозненных
> мыслей, я ими поделюсь, а дальше будем думать :)"""

                val expected = "15 апреля 2016 г., 20:26 пользователь Mariya Davydova < mariya.davydova@jetbrains.com> написал:"
                val actual = getQuoteHeaderLine(text)
                it("should fetch the first paragraph") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with some text") {
                val text = """Это в общих чертах, на деле, видимо, еще придется учесть несколько частных
случаев, но в целом, кажется, что задача поиска заголовка цитаты менее
масштабная. Признаки, кроме даты, не зависят от языка.

2. Я нашел пару библиотек для Java по нейросетям и ML
Neuroph <http://neuroph.sourceforge.net/index.html>  - по н.с.
Encog <http://www.heatonresearch.com/encog/> - по ML
поэтому есть возможность оставаться в контексте Java/Kotlin. Python для
всего этого, конечно, подходит лучше, но я с ним не работал, а на его
изучение уйдет какое-то доп. время. Вообще, я сейчас довольно много нового
материала впитываю, это может несколько замедлять процесс, но я стараюсь :)

Скажи, пожалуйста, что ты думаешь по поводу выделения заголовка цитаты по
дате/времени?

15 апреля 2016 г., 20:26 пользователь Mariya Davydova <
mariya.davydova@jetbrains.com> написал:

> Привет,
>
> У меня за последнее время родилось некоторое количество разрозненных
> мыслей, я ими поделюсь, а дальше будем думать :)"""

                val expected = "15 апреля 2016 г., 20:26 пользователь Mariya Davydova < mariya.davydova@jetbrains.com> написал:"
                val actual = getQuoteHeaderLine(text)
                it("should fetch quote header") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with some another text with some nested quotes") {
                val text = """Привет,

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
>> Sent from my iPad"""

                val expected = "On 03/02/2016 12:10 AM, Павел Жук wrote:"
                val actual = getQuoteHeaderLine(text)
                it("should fetch the first one") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with some another content text with some nested quotes") {
                val text = """цитата 2

13.03.2016, 01:01, "Паша Жук" <jojograf@yandex.ru>:
> цитата 1
>
> 13.03.2016, 01:01, "pav9147@yandex.ru" <pav9147@yandex.ru>:
>>  без цитаты"""

                val expected = """13.03.2016, 01:01, "Паша Жук" <jojograf@yandex.ru>:"""
                val actual = getQuoteHeaderLine(text)
                it("should fetch the first one") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with content text with ticked header") {
                val text = """ Reply из просто mail!


>Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук <ppzhuk@gmail.com>:
>
>Привет из gmail!
>
>--
>Жук Павел."""

                val expected = ">Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук <ppzhuk@gmail.com>:"
                val actual = getQuoteHeaderLine(text)
                it("should fetch that ticked header") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with FWD message") {
                val text = """-------- Перенаправленное сообщение --------
Тема: 	тема письма
Дата: 	Sun, 13 Mar 2016 01:01:33 +0300
От: 	pav9147@yandex.ru
Кому: 	jojograf@yandex.ru

без цитаты
"""

                val expected = "Тема: 	тема письма Дата: 	Sun, 13 Mar 2016 01:01:33 +0300 От: 	pav9147@yandex.ru Кому: 	jojograf@yandex.ru"
                val actual = getQuoteHeaderLine(text)
                it("should fetch all 4 fields as a quote header") {
                    assertEquals(expected, actual)
                }
            }

            on("calling with text without quotes") {
                val text = """Привет,

В принципе, я более-менее живая и в офисе. Если не напишу до 15-00,
встречаемся в 17-00 в офисе.

Скажи девочкам на ресепшене, что ты ко мне на практику, они либо
пропустят, либо позовут меня. Я сижу в 508 (кабинет прямо напротив
лифтовой площадки)."""

                it("should return null") {
                    assertNull(getQuoteHeaderLine(text))
                }
            }
        }
    }
}