package quoteParser

import org.jetbrains.spek.api.Given
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}error${File.separator}"

class QuoteParserErrorSpecs : Spek() {
    private val parser = QuoteParser.Builder()
            .deleteQuoteMarks(false)
            .build()

    private fun Given.check(emailNum: Int, expectedQuoteHeader: QuoteHeader?) {
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
            val emailNum = 136
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 0,
                    endIndex = 1,
                    text = listOf(
                            """12.05.2014 12:51, asdasd asdasd пишет:"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 166
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 2,
                    endIndex = 3,
                    text = listOf(
                            """Time: 2014-06-05 12:20 GMT+4 (MSK)"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 1729
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 10,
                    endIndex = 11,
                    text = listOf(
                            """In reply to:"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3549
            val expectedQuoteHeader: QuoteHeader? = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3953
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 0,
                    endIndex = 1,
                    text = listOf(
                            """slon slon slon slon at slon.slon.slon/slon is slon slon slon slon of """ +
                                    """11/25/2014 07:31 EST:"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 8863
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 33,
                    endIndex = 33,
                    text = listOf()
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 9757
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 2,
                    endIndex = 4,
                    text = listOf(
                            """On Fri, May 15, 2015 at 5:01 PM, text text <""",
                            """text.text@text.com> wrote:"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 17382
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 26,
                    endIndex = 27,
                    text = listOf(
                            """##- Please type your reply above this line -##"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 22912
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 4,
                    endIndex = 5,
                    text = listOf(
                            """INFO   | jvm 1    | 2016/07/05 16:27:17 | text:   text: """
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }
    }
}