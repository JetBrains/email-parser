package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Zhuk Pavel on 23.08.2016.
 */
class EmailFeatureSpecs : Spek() {
    init {
        val emailFeature = EmailFeature()

        given("string of text") {
            val s = "@lorem  ipsum dolor sit amet, 04/04/05, consectetuer 01:01:33 adipiscing elit. Aenean : "

            on("checking regexes") {
                it("should not match EMAIL regex") {
                    assertFalse {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "lorem@  ipsum dolor sit amet, 04/04/05, consectetuer 01:01:33 adipiscing elit. Aenean : "

            on("checking regexes") {
                it("should not match EMAIL regex") {
                    assertFalse {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "Lorem:  ipsum dolor sit amet, 1.2.2000 consectetuer @ adipiscing elit. Aenean "
            on("checking regexes") {
                it("should not match EMAIL regex") {
                    assertFalse {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "13. 2015, Lorem  ipsum dolor sit amet, consec@tetuer adipiscing elit. Aenean 12:34"
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "On 8 December 2014 at 15:16, superman <xxx@xxx.com> wrote:"
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """On Monday, September 15, 2014 6:50 AM, "yyyy@yyy.com" <yyyy@yyy.com> wrote:"""
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit amet - 3100 consectetuer adipiscing eli@t.cc"
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit amet - 3100 consectetuer adipiscing \"eli@t.cc\""
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit amet - 3100 consectetuer adipiscing /eli@t.cc/  "
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with tabulations") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor sit amet - 3100 consectetuer adipiscing \t\t\t\tp@gg.ru\t\t\t\t"
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """From:<eeee@eee.com> """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """From : "papa pa" < eeee@eee.com > """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """From "papa pa" <eeee@eee.com>: """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue {
                        emailFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17  Jul 2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue { emailFeature.matches(s) }
                }
            }
        }
        given("""string of text with non-breaking whitespaces and invisible(\\u200e) characters""") {
            // \u200e in email
            val s = """Reply: xxx   17  Jul 2015  13:25:18  eee‎-ddd@fff‎.com: """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue { emailFeature.matches(s) }
                }
            }
        }
        given("""string of text with non-breaking whitespaces and invisible(\\u200e) characters""") {
            // \u200e in email and around @
            val s = """Reply: xxx   17  Jul 2015  13:25:18  eee‎-ddd‎@‎fff‎.com: """
            on("checking regexes") {
                it("should match EMAIL regex") {
                    assertTrue { emailFeature.matches(s) }
                }
            }
        }
    }
}