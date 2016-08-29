package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Zhuk Pavel on 23.08.2016.
 */
class ColonFeatureSpecs : Spek() {
    init {
        val colonFeature = LastColonFeature()

        given("string of text") {
            val s = "@lorem  ipsum dolor sit amet, 04/04/05, consectetuer 01:01:33 adipiscing elit. Aenean : "

            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = "Lorem:  ipsum dolor sit amet, 1.2.2000 consectetuer @ adipiscing elit. Aenean "
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = "13. 2015, Lorem  ipsum dolor sit amet, consec@tetuer adipiscing elit. Aenean 12:34"
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = "On 8 December 2014 at 15:16, superman <xxx@xxx.com> wrote:"
            on("checking regexes") {
                it("should  match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit amet - 3100 consectetuer adipiscing eli@t.cc Aenean "
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = "> Date: 17 Jul 2015 13:25:18 GMT+3"
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = """From "papa pa" <eeee@eee.com>: """
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = """:From "papa pa" <eeee@eee.com>"""
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text") {
            val s = """Onderwerp: [XXX YYY] Re: xxx ccc vvv bbb"""
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17  Jul 2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("header phrase") {
            val s = """   In reply to:   """
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31"""
            on("checking regexes") {
                it("should not match COLON regex") {
                    assertFalse { colonFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31‎:"""
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31‎: """
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters and non-breaking whitespaces""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31‎ ‎:‎ """
            on("checking regexes") {
                it("should match COLON regex") {
                    assertTrue { colonFeature.matches(s) }
                }
            }
        }
    }
}