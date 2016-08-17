package quoteParser

import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParseSpecs : Spek() {
    private val path =
            ".${File.separator}src${File.separator}test${File.separator}" +
                    "resources${File.separator}contentTests${File.separator}"

    init {
        given("eml file") {
            on("simple email with signature") {
                val expectedContent = TestContent.simple_sig
                val actualContent = parse(File("${path}simple_sig.eml"))

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null header field") {
                    assertNull(actualContent.header)
                }
                it("should contain null quote field") {
                    assertNull(actualContent.quote)
                }
            }

            on("email with just quote") {
                val expectedContent = TestContent.simple_quote_koi8
                val actualContent = parse(File("${path}simple_quote_koi8.eml"))

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote!!.body, actualContent.quote!!.body)
                }
                it("should parse header correct") {
                    assertEquals(expectedContent.header.toString(), actualContent.header.toString())
                }
            }

            on("email with inner signature") {
                val expectedContent = TestContent.quote_plus_inner_sig
                val actualContent = parse(
                        File("${path}quote_plus_inner_sig.eml")
                )

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote!!.body, actualContent.quote!!.body)
                }
                it("should  parse header correct") {
                    assertEquals(expectedContent.header.toString(), actualContent.header.toString())
                }
            }

            on("email with some nested quotes") {
                val expectedContent = TestContent.only_nested_quotes
                val actualContent = parse(
                        File("${path}only_nested_quotes.eml")
                )
                println(actualContent)
                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote!!.body, actualContent.quote!!.body)
                }
                it("should  parse header correct") {
                    assertEquals(expectedContent.header.toString(), actualContent.header.toString())
                }
            }


            on("email with 5 nested quotes and signs") {
                val expectedContent = TestContent.quotes_signs_5
                var actualContent = parse(
                        File("${path}5_quotes_plus_signs.eml")
                )


            it("should parse body correct") {
                assertEquals(expectedContent.body, actualContent.body)
            }
            it("should parse quote correct") {
                assertEquals(expectedContent.quote!!.body, actualContent.quote!!.body)
            }
            it("should  parse header correct") {
                assertEquals(expectedContent.header.toString(), actualContent.header.toString())
            }
            }
        }
    }
}