package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Pavel.Zhuk on 24.08.2016.
 */
class QuoteMarkFeatureSpecs : Spek() {
    init {
        val quoteMarkFeature = QuoteMarkFeature()

        given("standalone gt symbol") {
            val s = ">"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("several > symbols") {
            val s = ">>>"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("several > symbols with whitespaces") {
            val s = "\t\t >  > >"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given(">text") {
            val s = ">text"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given(">>>text") {
            val s = ">>>text"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("\t >\t >  >\t\ttext") {
            val s = ">>>text"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("text with non-breaking whitespaces") {
            val s = " > > > text"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("text with non-breaking whitespaces and invisible(\\u200e) characters") {
            val s = " ‎>‎ > ‎‎>‎ text"

            on("checking regexes") {
                it("should match GREATER_THAN regex") {
                    assertTrue { quoteMarkFeature.matches(s) }
                }
            }
        }
        given("text without leading >") {
            val s = "text > text"

            on("checking regexes") {
                it("should not match GREATER_THAN regex") {
                    assertFalse { quoteMarkFeature.matches(s) }
                }
            }
        }

        given("several text lines") {
            val lines = listOf<String>(
                    "text > text",
                    ">   ",
                    ">text",
                    "\t> TEXT"
            )
            on("calling matchLines function") {
                val matching = quoteMarkFeature.matchLines(lines)
                it("must get appropriate value") {
                    assertTrue { matching[0] == QuoteMarkMatchingResult.NOT_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[1] == QuoteMarkMatchingResult.V_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[2] == QuoteMarkMatchingResult.V_NOT_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[3] == QuoteMarkMatchingResult.V_NOT_EMPTY }
                }
            }
        }

        given("several text lines") {
            val lines = listOf<String>(
                    ">text > text",
                    " \t  ",
                    "\t>  > TEXT",
                    "\t>  ",
                    "\t TEXT"
            )
            on("calling matchLines function") {
                val matching = quoteMarkFeature.matchLines(lines)
                it("must get appropriate value") {
                    assertTrue { matching[0] == QuoteMarkMatchingResult.V_NOT_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[1] == QuoteMarkMatchingResult.EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[2] == QuoteMarkMatchingResult.V_NOT_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[3] == QuoteMarkMatchingResult.V_EMPTY }
                }
                it("must get appropriate value") {
                    assertTrue { matching[4] == QuoteMarkMatchingResult.NOT_EMPTY }
                }
            }
        }
    }
}