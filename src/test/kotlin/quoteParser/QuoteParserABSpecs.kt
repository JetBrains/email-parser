package quoteParser

import org.jetbrains.spek.api.Given
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}AB${File.separator}"

class QuoteParserABSpecs : Spek() {
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
            val emailNum = 149
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 5,
                    endIndex = 5,
                    text = listOf()
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 4237
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 16,
                    endIndex = 16,
                    text = listOf()
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 5370
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 10,
                    endIndex = 10,
                    text = listOf()
            )
            check(emailNum, expectedQuoteHeader)
        }
    }
}