package quoteParser

import org.jetbrains.spek.api.Given
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}empty${File.separator}"

class QuoteParserEmptySpecs : Spek() {
    private val parser = QuoteParser.Builder().
            deleteQuoteMarks(false).
            build()

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
            val emailNum = 93
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 102
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 146
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 205
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 1165
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 3505
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 20454
            val expectedQuoteHeader = null
            check(emailNum, expectedQuoteHeader)
        }
    }
}