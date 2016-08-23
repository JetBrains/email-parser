package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Zhuk Pavel on 23.08.2016.
 */
class MiddleColonFeatureSpecs : Spek() {
    init {
        val middleColonFeature = MiddleColonFeature()
        
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit amet - 3100 consectetuer adipiscing eli@t.cc Aenean "
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "lorem ipsum dolor sit amet - 3100 consectetuer adipiscing eli@t.cc Aenean: "
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "> Date: 17 Jul 2015 13:25:18 GMT+3"
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "lorem ipsum dolor sit amet - 3100 12:34:\t\t\t\t  consectetuer adipiscing \t\t\t\tc@f.rt\t\t\t\t"
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """     From: "papa pa" <eeee@eee.com> """
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """From : "papa pa" <eeee@eee.com> """
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """From "papa pa" <eeee@eee.com>: """
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """:From "papa pa" <eeee@eee.com>"""
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """Onderwerp: [XXX YYY] Re: xxx ccc vvv bbb"""
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17  Jul 2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters""") {
            val s = """From‎ ‎:‎ "papa pa" <eeee@eee.com> """
            on("checking regexes") {
                it("should match MIDDLE_COLON regex") {
                    assertTrue {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
    }
}