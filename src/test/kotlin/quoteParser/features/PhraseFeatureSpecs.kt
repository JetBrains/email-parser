package quoteParser.features

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Zhuk Pavel on 23.08.2016.
 */
class PhraseFeatureSpecs : Spek() {
    init {
        val phraseFeature = PhraseFeature()

        given("header phrase") {
            val s = """   In reply to:   """
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase") {
            val s = """>> In reply to:   """
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase") {
            val s = """   In    RePly To:   """
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase") {
            val s = """##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -##"""
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }

        given("header phrase") {
            val s = """##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -##"""
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }

        given("header phrase") {
            val s = """> > ##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -##"""
            on("checking regexes") {
                it("should not match PHRASE regex") {
                    assertFalse { phraseFeature.matches(s) }
                }
            }
        }

        given("""header phrase with non-breaking whitespaces and invisible characters(u200e)""") {
            val s = """   In    RePly To:   """
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase with non-breaking whitespaces and invisible character(u200e)") {
            val s = """ ‎##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -## """
            on("checking regexes") {
                it("should match PHRASE regex") {
                    assertTrue { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase with some garbage") {
            val s = """ >>>>  In    RePly To:  <<< """
            on("checking regexes") {
                it("should not match PHRASE regex") {
                    assertFalse { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase with some garbage") {
            val s = """text ##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -##"""
            on("checking regexes") {
                it("should not match PHRASE regex") {
                    assertFalse { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase with some garbage") {
            val s = """  In    RePly To:  garbage"""
            on("checking regexes") {
                it("should not match PHRASE regex") {
                    assertFalse { phraseFeature.matches(s) }
                }
            }
        }
        given("header phrase with some garbage") {
            val s = """##- Bitte geben Sie Ihre Antwort oberhalb dieser Zeile ein. -## garbage"""
            on("checking regexes") {
                it("should not match PHRASE regex") {
                    assertFalse { phraseFeature.matches(s) }
                }
            }
        }
    }
}