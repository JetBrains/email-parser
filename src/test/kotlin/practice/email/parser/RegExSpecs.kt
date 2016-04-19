package practice.email.parser

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegExSpecs : Spek() {
    init {
        given("2016, token") {
            val token = Token("2016,")
            on("it's creation") {
                it("has true isDigits") {
                    assertTrue { token.isDigits }
                }
                it("has true isLastComma") {
                    assertTrue { token.hasLastComma } 
                }
                it("has false isWithAngleBrackets") {
                    assertFalse { token.hasWithAngleBrackets }
                }
                it("has false isEmail") {
                    assertFalse { token.isEmail }
                }
                it("has false isTime") {
                    assertFalse { token.isTime }
                }
                it("has false isMeridiem") {
                    assertFalse { token.isMeridiem }
                }
            }
        }

        given("email token") {
            val token = Token("<mariya.davydova@jetbrains.com>")
            on("it's creation") {
                it("has false isDigits") {
                    assertFalse { token.isDigits }
                }
                it("has false isLastComma") {
                    assertFalse { token.hasLastComma }
                }
                it("has true isWithAngleBrackets") {
                    assertTrue { token.hasWithAngleBrackets }
                }
                it("has true isEmail") {
                    assertTrue { token.isEmail }
                }
                it("has false isTime") {
                    assertFalse { token.isTime }
                }
                it("has false isMeridiem") {
                    assertFalse { token.isMeridiem }
                }
            }
        }

        given("email token without brackets") {
            val token = Token("ppzhuk@gmail.com")
            on("it's creation") {
                it("has false isDigits") {
                    assertFalse { token.isDigits }
                }
                it("has false isLastComma") {
                    assertFalse { token.hasLastComma }
                }
                it("has false isWithAngleBrackets") {
                    assertFalse { token.hasWithAngleBrackets }
                }
                it("has true isEmail") {
                    assertTrue { token.isEmail }
                }
                it("has false isTime") {
                    assertFalse { token.isTime }
                }
                it("has false isMeridiem") {
                    assertFalse { token.isMeridiem }
                }
            }
        }

        given("time token") {
            val token = Token("00:00")
            on("it's creation") {
                it("has false isDigits") {
                    assertFalse { token.isDigits }
                }
                it("has false isLastComma") {
                    assertFalse { token.hasLastComma }
                }
                it("has false isWithAngleBrackets") {
                    assertFalse { token.hasWithAngleBrackets }
                }
                it("has false isEmail") {
                    assertFalse { token.isEmail }
                }
                it("has true isTime") {
                    assertTrue { token.isTime }
                }
                it("has false isMeridiem") {
                    assertFalse { token.isMeridiem }
                }
            }
        }
        
        given("incorrect time token") {
            val token = Token("+10:00")
            on("it's creation") {
                it("has false isTime") {
                    assertFalse { token.isTime }
                }
            }
        }

        given("meridiem token") {
            val token = Token("PM")
            on("it's creation") {
                it("has true isMeridiem") {
                    assertTrue { token.isMeridiem }
                }
            }
        }

        given("another meridiem token") {
            val token = Token("a.m.,")
            on("it's creation") {
                it("has true isMeridiem") {
                    assertTrue { token.isMeridiem }
                }
            }
        }

        given("date1 token") {
            val token = Token("2006.05.04")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date2 token") {
            val token = Token("2006-5-4")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date3 token") {
            val token = Token("2006/5/4")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date4 token") {
            val token = Token("4.5.2006")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date5 token") {
            val token = Token("4-5-2006")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date6 token") {
            val token = Token("4/5/2006")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date7 token") {
            val token = Token("04.05.2006")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date8 token") {
            val token = Token("04-05-2006")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
            }
        }

        given("date9 token") {
            val token = Token("04/05/2006,")
            on("it's creation") {
                it("has true isDate") {
                    assertTrue { token.isDate }
                }
                it("has true isLastComma") {
                    assertTrue { token.hasLastComma }
                }
            }
        }

        given("last token with column") {
            val token = Token("написал:\n")
            on("it's creation") {
                it("has true isLastTokenColumn") {
                    assertTrue { token.hasLastTokenColumn }
                }
            }
        }

        given("last token") {
            val token = Token("wrote\n")
            on("it's creation") {
                it("has true isLastToken") {
                    assertTrue { token.isLastToken }
                }
            }
        }
    }
}