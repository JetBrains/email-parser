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
    }
}