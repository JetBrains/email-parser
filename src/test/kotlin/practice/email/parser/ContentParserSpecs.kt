package practice.email.parser

import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentParserSpecs : Spek() {
    private val path = 
            ".${File.separator}src${File.separator}test${File.separator}" + 
            "resources${File.separator}contentTests${File.separator}"
    
    init {
        given("email content parser with parseMode ONE") {
            on("simple email with signature") {
                val expectedContent = TestContents.simple_sig
                var actualContent = EmailParser(
                        File("${path}simple_sig.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote field") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }
            }
            
            on("second simple email with signature") {
                val expectedContent = TestContents.simple_sig_2
                var actualContent = EmailParser(
                        File("${path}simple_sig_2.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote field") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }
            }

            on("email with just quote") {
                val expectedContent = TestContents.simple_quote_koi8
                val actualContent = EmailParser(
                        File("${path}simple_quote_koi8.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should contain null signature field") {
                    assertNull(actualContent.signature)
                }
            }

            on("email with inner signature") {
                val expectedContent = TestContents.quote_plus_inner_sig
                val actualContent = EmailParser(
                        File("${path}quote_plus_inner_sig.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should contain null signature field") {
                    assertNull(actualContent.signature)
                }
            }

            on("email with some nested quotes") {
                val expectedContent = TestContents.only_nested_quotes
                val actualContent = EmailParser(
                        File("${path}only_nested_quotes.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should contain null signature field") {
                    assertNull(actualContent.signature)
                }
            }

            on("simple email with just body") {
                val expectedContent = TestContents.just_body
                var actualContent = EmailParser(
                        File("${path}just_body.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote field") {
                    assertNull(actualContent.quote)
                }
                it("should contain null signature field") {
                    assertNull(actualContent.signature)
                }
            }

            on("email with 5 nested quotes and signs") {
                val expectedContent = TestContents.quotes_signs_5
                var actualContent = EmailParser(
                        File("${path}5_quotes_plus_signs.eml"),
                        ContentParseMode.MODE_ONE
                ).parse().content

                it("should parse body correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should parse signature correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }
            }
        }
        
        given("email content parser with parseMode DEEP") {
            on("simple email with signature") {
                val expectedContent = TestContents.simple_sig_deep
                val actualContent = EmailParser(
                        File("${path}simple_sig.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote0 field") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature0 correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }
            }

            on("second simple email with signature") {
                val expectedContent = TestContents.simple_sig_2_deep
                val actualContent = EmailParser(
                        File("${path}simple_sig_2.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote0 field") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature0 correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }
            }

            on("email with just quote") {
                val expectedContent = TestContents.simple_quote_koi8_deep
                val actualContent = EmailParser(
                        File("${path}simple_quote_koi8.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote0 correct") {
                    assertEquals("${expectedContent.quote?.body}\n", actualContent.quote?.body)
                }
                it("should contain null signature0 field") {
                    assertNull(actualContent.signature)
                }

                it("should parse body1 correct") {
                    assertEquals("${expectedContent.quote?.body}\n", actualContent.quote?.body)
                }
                it("should contain null quote1 field") {
                    assertNull(actualContent.quote?.quote)
                }
                it("should contain null signature1 field") {
                    assertNull(actualContent.quote?.signature)
                }
            }

            on("email with inner signature") {
                val expectedContent = TestContents.quote_plus_inner_sig_deep
                var actualContent = EmailParser(
                        File("${path}quote_plus_inner_sig.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote0 correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should contain null signature0 field") {
                    assertNull(actualContent.signature)
                }

                it("should parse body1 correct") {
                    assertEquals(expectedContent.quote?.body, actualContent.quote?.body)
                }
                it("should contain null quote1 field") {
                    assertNull(actualContent.quote?.quote)
                }
                it("should parse signature1 correct") {
                    assertEquals(expectedContent.quote?.signature, actualContent.quote?.signature)
                }
            }
            
            on("email with some nested quotes") {
                val expectedContent = TestContents.only_nested_quotes_deep
                val actualContent = EmailParser(
                        File("${path}only_nested_quotes.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote0 correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should contain null signature0 field") {
                    assertNull(actualContent.signature)
                }

                var innerActualContent: Content? = actualContent.quote
                var innerExpectedContent: Content? = expectedContent.quote
                it("should parse body1 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should parse quote1 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }
                it("should contain null signature1 field") {
                    assertNull(innerActualContent?.signature)
                }

                innerActualContent = innerActualContent?.quote
                innerExpectedContent = innerExpectedContent?.quote
                it("should parse body2 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should contain null quote2 field") {
                    assertNull(innerActualContent?.quote)
                }
                it("should contain null signature2 field") {
                    assertNull(innerActualContent?.signature)
                }
            }

            on("simple email with just body") {
                val expectedContent = TestContents.just_body_deep
                val actualContent = EmailParser(
                        File("${path}just_body.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should contain null quote0 field") {
                    assertNull(actualContent.quote)
                }
                it("should contain null signature0 field") {
                    assertNull(actualContent.signature)
                }
            }

            on("email with 5 nested quotes and signs") {
                val expectedContent = TestContents.quotes_signs_5_deep
                val actualContent = EmailParser(
                        File("${path}5_quotes_plus_signs.eml"),
                        ContentParseMode.MODE_DEEP
                ).parse().content

                it("should parse body0 correct") {
                    assertEquals(expectedContent.body, actualContent.body)
                }
                it("should parse quote0 correct") {
                    assertEquals(expectedContent.quote, actualContent.quote)
                }
                it("should parse signature0 correct") {
                    assertEquals(expectedContent.signature, actualContent.signature)
                }

                var innerActualContent: Content? = actualContent.quote
                var innerExpectedContent: Content? = expectedContent.quote
                it("should parse body1 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should parse quote1 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }
                it("should contain null signature1 field") {
                    assertNull(innerActualContent?.signature)
                }

                innerActualContent = innerActualContent?.quote
                innerExpectedContent = innerExpectedContent?.quote
                it("should parse body2 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should parse quote2 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }
                it("should contain null signature2 field") {
                    assertNull(innerActualContent?.signature)
                }

                innerActualContent = innerActualContent?.quote
                innerExpectedContent = innerExpectedContent?.quote
                it("should parse body3 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should parse quote3 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }
                it("should parse signature3 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }

                innerActualContent = innerActualContent?.quote
                innerExpectedContent = innerExpectedContent?.quote
                it("should parse body4 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should parse quote4 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }
                it("should parse signature4 correct") {
                    assertEquals(innerExpectedContent?.quote, innerActualContent?.quote)
                }

                innerActualContent = innerActualContent?.quote
                innerExpectedContent = innerExpectedContent?.quote
                it("should parse body5 correct") {
                    assertEquals(innerExpectedContent?.body, innerActualContent?.body)
                }
                it("should contain null quote5 field") {
                    assertNull(innerActualContent?.quote)
                }
                it("should contain null signature5 field") {
                    assertNull(innerActualContent?.signature)
                }
            }
        }
    }
}