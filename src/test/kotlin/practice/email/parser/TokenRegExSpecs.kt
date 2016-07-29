package practice.email.parser

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TokenRegExSpecs : Spek() {
    init {
        given("2016, token") {
            val token = Token("2016,")
            on("it's creation") {
                it("has DIGITS type") {
                    assertEquals(TokenType.DIGITS, token.type)
                }
                it("has true lastComma") {
                    assertTrue { token.attrs.lastComma }
                }
                it("has false withAngleBrackets") {
                    assertFalse { token.attrs.withAngleBrackets }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("email token") {
            val token = Token("<mdfg.dfgg@cvbvc.com>")
            on("it's creation") {
                it("has EMAIL type") {
                    assertEquals(TokenType.EMAIL, token.type)
                }
                it("has false lastComma") {
                    assertFalse { token.attrs.lastComma }
                }
                it("has true withAngleBrackets") {
                    assertTrue { token.attrs.withAngleBrackets }
                }
                it("has true hasAtSymbol") {
                    assertTrue { token.attrs.hasAtSymbol }
                }
                it("has false nonAlphabetic") {
                    assertFalse { token.attrs.nonAlphabetic }
                }
            }
        }

        given("@ token") {
            val token = Token("@")
            on("it's creation") {
                it("has UNDEFINED type") {
                    assertEquals(TokenType.UNDEFINED, token.type)
                }
                it("has true hasAtSymbol") {
                    assertTrue { token.attrs.hasAtSymbol }
                }
                it("has true nonLetterOrDigit") {
                    assertTrue { token.attrs.nonLetterOrDigit }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("@ppzhuk token") {
            val token = Token("@ppzhuk")
            on("it's creation") {
                it("has UNDEFINED type") {
                    assertEquals(TokenType.UNDEFINED, token.type)
                }
                it("has true hasAtSymbol") {
                    assertTrue { token.attrs.hasAtSymbol }
                }
            }
        }

        given("xyz@ppzhuk token") {
            val token = Token("xyz@ppzhuk")
            on("it's creation") {
                it("has EMAIL type") {
                    assertEquals(TokenType.EMAIL, token.type)
                }
                it("has true hasAtSymbol") {
                    assertTrue { token.attrs.hasAtSymbol }
                }
            }
        }

        given("email token without brackets") {
            val token = Token("ppzh@mail.com")
            on("it's creation") {
                it("has EMAIL type") {
                    assertEquals(TokenType.EMAIL, token.type)
                }
                it("has false lastComma") {
                    assertFalse { token.attrs.lastComma }
                }
                it("has false withAngleBrackets") {
                    assertFalse { token.attrs.withAngleBrackets }
                }
            }
        }

        given("time token") {
            val token = Token("00:00")
            on("it's creation") {
                it("has TIME type") {
                    assertEquals(TokenType.TIME, token.type)
                }
                it("has false lastComma") {
                    assertFalse { token.attrs.lastComma }
                }
                it("has false withAngleBrackets") {
                    assertFalse { token.attrs.withAngleBrackets }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("time token") {
            val token = Token("3:38:09")
            on("it's creation") {
                it("has TIME type") {
                    assertEquals(TokenType.TIME, token.type)
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("time token") {
            val token = Token("13:16:")
            on("it's creation") {
                it("has TIME type") {
                    assertEquals(TokenType.TIME, token.type)
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
                it("has true lastColon") {
                    assertTrue { token.attrs.lastColon }
                }
            }
        }

        given("time+sec token") {
            val token = Token("23:59:03,")
            on("it's creation") {
                it("has TIME type") {
                    assertEquals(TokenType.TIME, token.type)
                }
                it("has false lastComma") {
                    assertTrue { token.attrs.lastComma }
                }
                it("has false withAngleBrackets") {
                    assertFalse { token.attrs.withAngleBrackets }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("incorrect time token") {
            val token = Token("+10:00")
            on("it's creation") {
                it("has not TIME type") {
                    assertNotEquals(TokenType.TIME, token.type)
                }
                it("has UNDEFINED type") {
                    assertEquals(TokenType.UNDEFINED, token.type)
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given("meridiem token") {
            val token = Token("PM")
            on("it's creation") {
                it("has MERIDIEM type") {
                    assertEquals(TokenType.MERIDIEM, token.type)
                }
            }
        }

        given("another meridiem token") {
            val token = Token("a.m.,")
            on("it's creation") {
                it("has MERIDIEM type") {
                    assertEquals(TokenType.MERIDIEM, token.type)
                }
            }
        }

        given("date1 token") {
            val token = Token("2006.05.04")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date2 token") {
            val token = Token("2006-5-4")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date3 token") {
            val token = Token("2006/5/4")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date4 token") {
            val token = Token("4.5.2006")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date5 token") {
            val token = Token("4-5-2006")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date6 token") {
            val token = Token("4/5/2006")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date7 token") {
            val token = Token("04.05.2006")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date8 token") {
            val token = Token("04-05-2006")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
            }
        }

        given("date9 token") {
            val token = Token("04/05/2006,")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
                it("has true lastComma") {
                    assertTrue { token.attrs.lastComma }
                }
            }
        }

        given("date9 token") {
            val token = Token("2006/04/05,")
            on("it's creation") {
                it("has DATE type") {
                    assertEquals(TokenType.DATE, token.type)
                }
                it("has true lastComma") {
                    assertTrue { token.attrs.lastComma }
                }
            }
        }

        given("token with colon") {
            val token = Token("wrote:")
            on("it's creation") {
                it("has true lastColon") {
                    assertTrue { token.attrs.lastColon }
                }
            }
        }

        given("non letter or digit token") {
            val token = Token("""}{[]\|\|?/.,><';:"~`""")
            on("it's creation") {
                it("has UNDEFINED type") {
                    assertEquals(TokenType.UNDEFINED, token.type)
                }
                it("has true nonLetterOrDigit") {
                    assertTrue { token.attrs.nonLetterOrDigit }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }

        given(">: token") {
            val token = Token(">:")
            on("it's creation") {
                it("has UNDEFINED type") {
                    assertEquals(TokenType.UNDEFINED, token.type)
                }
                it("has true nonLetterOrDigit") {
                    assertTrue { token.attrs.nonLetterOrDigit }
                }
                it("has true lastColon") {
                    assertTrue { token.attrs.lastColon }
                }
                it("has true nonAlphabetic") {
                    assertTrue { token.attrs.nonAlphabetic }
                }
            }
        }
    }
}