/*
 * Copyright 2016-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateFeatureSpecs : Spek() {
    init {
        val dateFeature = DateFeature()
        
        given("string of text") {
            val s = "@lorem  ipsum dolor sit amet, 04/04/05, consectetuer 01:01:33 adipiscing elit. Aenean : "

            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
                
            }
        }
        given("string of text") {
            val s = "Lorem:  ipsum dolor sit amet, 1.2.2000. consectetuer @ adipiscing elit. Aenean "
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "13. 2015, Lorem  ipsum dolor sit amet, consec@tetuer adipiscing elit. Aenean 12:34"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "On 8 December 2014 at 15:16, superman <xxx@xxx.com> wrote:"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """On Monday, September 15, 2014. 6:50 AM, "yyyy@yyy.com" <yyyy@yyy.com> wrote:"""
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """> On 05 Nov smth 2014, at 00:30:33, yyyy@yyy.com wrote:"""
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """On Monday, September 2014, 15,  6:50 AM, "yyyy@yyy.com" <yyyy@yyy.com> wrote:"""
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = """> On 2014 Nov smth 05, at 00:30:33, yyyy@yyy.com wrote:"""
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit 12 amet - 2100 consectetuer adipiscing eli@t.cc Aenean "
            on("checking regexes") {
                it("should not match DATE regex") {
                    assertFalse {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34, lorem ipsum dolor sit 01 May 1999, consectetuer adipiscing eli@t.cc Aenean "
            on("checking regexes") {
                it("should not match DATE regex (only 2k+ year)") {
                    assertFalse {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with prefix colon") {
            val s = "> Date:17 Jul 2015 13:25:18 GMT+3"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with prefix colon") {
            val s = "> Date:2000-01-15. 13:25:18 GMT+3"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string with date with brackets") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor sit (01 may 2016). consectetuer adipiscing \t\t\t\tpa7@yx.ru\t\t\t\t"
            on("checking regexes") {
                it("should not match DATE regex") {
                    assertFalse {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string with late date") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor sit consectetuer adipiscing \t\t\t\tpa7@yx.ru 01 may 2016:"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string without date") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor Feb 2016. consectetuer adipiscing \t\t\t\tp@x.ru\t\t\t\t"
            on("checking regexes") {
                it("should not match DATE regex") {
                    assertFalse {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("phone number string") {
            val s = "text text: +7(962)12-03-2000"
            on("checking regexes") {
                it("should not match DATE regex") {
                    assertFalse {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor sit 01 de may de 2016. consectetuer adipiscing \t\t\t\tp@c.tu\t\t\t\t"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "12:34,\t\t\t\t lorem ipsum dolor sit 2016, de may de 04 consectetuer adipiscing \t\t\t\tp@c.tu\t\t\t\t"
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue {
                        dateFeature.matches(s)
                    }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17-Jul-2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should not match DATE regex") {
                    assertFalse { dateFeature.matches(s) }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17 Jul 2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue { dateFeature.matches(s) }
                }
            }
        }
        given("string of text with non-breaking whitespaces") {
            val s = """Reply: xxx   17-01-2015  13:25:18  eee-ddd@fff.com: """
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue { dateFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31"""
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue { dateFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters and non-breaking whitespaces""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎. ‎Oktober‎ ‎2014 ‎18‎:‎31‎ ‎:‎ """
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue { dateFeature.matches(s) }
                }
            }
        }
        given("""string of text with invisible(\\u200e) characters and non-breaking whitespaces""") {
            val s = """Gesendet: Donnerstag‎, ‎30‎-‎08‎-‎2014 ‎18‎:‎31‎ ‎:‎ """
            on("checking regexes") {
                it("should match DATE regex") {
                    assertTrue { dateFeature.matches(s) }
                }
            }
        }
    }
}