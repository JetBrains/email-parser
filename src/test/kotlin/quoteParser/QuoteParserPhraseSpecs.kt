package quoteParser

import org.jetbrains.spek.api.Given
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}phrases${File.separator}"

class QuoteParserPhraseSpecs : Spek() {
    private val parser = QuoteParser.Builder().
            deleteQuoteMarks(false).
            build()

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
            val emailNum = 92
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 9,
                    endIndex = 10,
                    text = listOf(
                            """In reply to:"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 2555
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 2,
                    endIndex = 3,
                    text = listOf(
                            """-------- Original message --------"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }

        given("eml file") {
            val emailNum = 17407
            val expectedQuoteHeader = QuoteHeader(
                    startIndex = 1,
                    endIndex = 2,
                    text = listOf(
                            """##- Please type your reply above this line -##"""
                    )
            )
            check(emailNum, expectedQuoteHeader)
        }
    }
}