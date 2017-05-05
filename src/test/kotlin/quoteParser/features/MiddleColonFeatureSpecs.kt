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
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = ":lorem ipsum dolor sit amet"
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
                        middleColonFeature.matches(s)
                    }
                }
            }
        }
        given("string of text") {
            val s = "\t \t :lorem ipsum dolor sit amet"
            on("checking regexes") {
                it("should not match MIDDLE_COLON regex") {
                    assertFalse {
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