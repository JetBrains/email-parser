package quoteParser

import org.jetbrains.spek.api.Given
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}ML${File.separator}"

class QuoteParserMLSpecs : Spek() {
    private val parser = QuoteParser.Builder()
            .deleteQuoteMarks(false)
            .build()

    private fun Given.check(emailNum: Int, expectedQuoteHeader: QuoteHeader) {
        val url = parser.javaClass.classLoader.getResource("$dir$emailNum.eml")
        val file = File(url.toURI())
        on("processing it") {
            val content = parser.parse(file)
            it("must define a quote") {
                assertEquals(expectedQuoteHeader, content.header)
            }
        }
    }

    init {
        given("eml file") {
            val emailNum = 31
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 22,
                    endIndex = 26,
                    text = listOf(
                            "From: text text [mailto:text-text@text.com] ",
                            "Sent: Monday, May 26, 2014 11:13 AM",
                            "To: text@text.com",
                            "Subject: [text-6432] text text text, text to text in to text.com"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 38
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 16,
                    endIndex = 21,
                    text = listOf(
                            "From: text text [mailto:text.text@text.com]",
                            "Sent: Tuesday, May 27, 2014 4:05 AM",
                            "To: text text",
                            "Cc: text-text-v@text.com; text.text@v.com",
                            "Subject: Re: text text v"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 43
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 37,
                    endIndex = 41,
                    text = listOf(
                            "От:     text text <text-text@text.com>",
                            "Кому:   text@text.ru",
                            "Дата:   26.05.2014 21:27",
                            "Тема:   [text-9654] Организация text'a"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 82
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 14,
                    endIndex = 16,
                    text = listOf(
                            "<text-text@text.com>",
                            "Дата:   30.05.2014 11:17"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 85
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 31,
                    endIndex = 34,
                    text = listOf(
                            "From: text text [mailto:text-text@text.com] ",
                            "Sent: Thursday, May 29, 2014 5:01 PM",
                            "Subject: Re: text text text text"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 158
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 2,
                    endIndex = 7,
                    text = listOf(
                            "From: text text text [mailto:text.text@text.com]",
                            "Sent: Wednesday, June 04, 2014 12:20 PM",
                            "To: 'text text'",
                            "Cc: text text; text text",
                            "Subject: FW: text text in text- can't text text text"
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 196
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 5,
                    endIndex = 9,
                    text = listOf(
                            """From: text text""",
                            """Sent: Wednesday, June 04, 2014 2:46 PM""",
                            """To: 'text text'""",
                            """Subject: RE: text-text text text text text tex textext"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 210
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 26,
                    endIndex = 32,
                    text = listOf(
                            """From: text text [mailto:text.text@text.com] """,
                            """Sent: Thursday, June 05, 2014 2:11 PM""",
                            """To: text.text@text.com""",
                            """Cc: text text; text text""",
                            """Subject: text of text text text & text""",
                            """Importance: High"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 260
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 15,
                    endIndex = 20,
                    text = listOf(
                            """From: test test [mailto:test-test@test.com]""",
                            """Sent: Tuesday, June 10, 2014 12:11 PM""",
                            """To: test test""",
                            """Cc: test test""",
                            """Subject: Re: test test test"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 279
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 26,
                    endIndex = 27,
                    text = listOf(
                            """Sent: Tue 27 May 2014 18:46"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 322
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 9,
                    endIndex = 13,
                    text = listOf(
                            """Da:     text text <text-text@text.com>""",
                            """Per:    text.text@text.it""",
                            """Data:   11/06/2014 19.16""",
                            """Oggetto:        Re: text text text?"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 348
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 21,
                    endIndex = 23,
                    text = listOf(
                            """<text-text-text@text.com>""",
                            """Data:   14/06/2014 12.41"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3697
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 18,
                    endIndex = 23,
                    text = listOf(
                            """> """,
                            """> From: test test [mailto:test@test.com <mailto:test@test.com>] """,
                            """> Sent: Friday, November 14, 2014 8:40 AM""",
                            """> To: test@test.com <mailto:test@test.com>""",
                            """> Subject: test test test to test my test test for test test test test"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3715
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 15,
                    endIndex = 23,
                    text = listOf(
                            """>""",
                            """>""",
                            """>""",
                            """>""",
                            """>""",
                            """>""",
                            """> *From:* text text [mailto:text.text@text.com]""",
                            """> *Sent:* Thursday, November 13, 2014 11:15 AM"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3926
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 2,
                    endIndex = 8,
                    text = listOf(
                            """From:  test test <test.test@test.com>""",
                            """Organization:  test test""",
                            """Date:  Monday, November 24, 2014 at 4:05 PM""",
                            """To:  'test test' <test@test.net>, <test@test.com>""",
                            """Cc:  test test <test@test.com>""",
                            """Subject:  RE: test test test 1500 test test test"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 4298
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 10,
                    endIndex = 11,
                    text = listOf(
                            """<div>-------- Original message --------</div><div>From: text text """ +
                                    """<text-text@text.com> </div><div>Date:12/02/2014  09:48  (GMT-08:00) """ +
                                    """</div><div>To: text.text.123@text.text.text </div><div>Subject: Re: text't""" +
                                    """ text or text text </div><div>"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 5859
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 3,
                    endIndex = 6,
                    text = listOf(
                            """From : asd.text@text.com""",
                            """Sent : Sun Nov 02 17:11:48 MSK 2014""",
                            """Subject : text text to text text text"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 17326
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 11,
                    endIndex = 17,
                    text = listOf(
                            """> Begin forwarded message:""",
                            """> """,
                            """> From: text text <text@text.com>""",
                            """> Subject: text R123456. text text text text""",
                            """> Date: 19 October 2015 16:38:23 BST""",
                            """> To: text@text.text.uk"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 17747
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 19,
                    endIndex = 21,
                    text = listOf(
                            """text.text@text.com]""",
                            """*Sent:* Friday, October 30, 2015 2:58 PM"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 18139
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 85,
                    endIndex = 89,
                    text = listOf(
                            """From: text, text P.""",
                            """Sent: Friday, November 06, 2015 3:44 PM""",
                            """To: 'text text'""",
                            """Subject: RE: [text text] Re: text text text text - text text text text text"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }


    }
}