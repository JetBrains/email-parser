package practice.email.parser

import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Assert.assertArrayEquals

class MailParserSpecs : Spek() {

    init {
        given("email parser with SIMPLE parse mode") {
            on("simple email") {
                val expectedEmail: Email = TestEmails.simple
                val actualEmail: Email = parseEml(
                        File(""".\src\test\resources\emailTests\simple.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("email with KOI8-R content-type charset") {
                val expectedEmail = TestEmails.simple_koi8
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\simple_koi8.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("email with base64 Content-Transfer-Encoding") {
                val expectedEmail = TestEmails.simple_base64_encoding
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\simple_base64_encoding.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("another email with KOI8-R content-type charset") {
                val expectedEmail = TestEmails.simple_koi8_2
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\simple_koi8_2.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("FWD multipart/alternative email") {
                val expectedEmail = TestEmails.multipart_alt
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\multipart_alt.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("FWD multipart/alternative KOI8-R email") {
                val expectedEmail = TestEmails.multipart_alt_koi8
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\multipart_alt_koi8.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("Outlook multipart/alternative US-ASCII email") {
                val expectedEmail = TestEmails.multipart_alt_ascii
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\multipart_alt_ascii_CONTENT_TYPE_SEPARATE_LINES_and_CHARSET_QUOTES.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("Outlook multipart/alternative KOI8-R email") {
                val expectedEmail = TestEmails.multipart_alt_koi8_2
                val actualEmail = parseEml(
                        File(""".\src\test\resources\emailTests\multipart_alt_koi8_CONTENT_TYPE_SEPARATE_LINES_and_CHARSET_QUOTES.eml""")
                )

                it("should parse DATE correct") {
                    assertEquals(expectedEmail.date, actualEmail.date)
                }
                it("should parse FROM addresses correct") {
                    assertArrayEquals(expectedEmail.from.toArray(), actualEmail.from.toArray())
                }
                it("should parse TO addresses correct") {
                    assertArrayEquals(expectedEmail.to.toArray(), actualEmail.to.toArray())
                }
                it("should parse SUBJECT correct") {
                    assertEquals(expectedEmail.subject, actualEmail.subject)
                }
                it("should parse CONTENT BODY correct") {
                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }
        }
    }
}