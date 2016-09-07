package practice.email.parser

import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertArrayEquals
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MailParserSpecs : Spek() {
    private val path =
            ".${File.separator}src${File.separator}test${File.separator}" +
                    "resources${File.separator}emailTests${File.separator}"

    init {
        given("email parser with SIMPLE parse mode") {
            on("simple email") {
                val expectedEmail: Email = TestEmails.simple
                val actualEmail: Email = EmailParser(
                        File("${path}simple.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}simple_koi8.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}simple_base64_encoding.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}simple_koi8_2.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}multipart_alt.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}multipart_alt_koi8.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}multipart_alt_ascii.eml")
                ).parse()

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
                val actualEmail = EmailParser(
                        File("${path}multipart_alt_koi8_2.eml")
                ).parse()

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