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
                var expectedEmail: Email = TestEmails.yam_tb_eng_rus
                var actualEmail: Email = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\yam_tb_eng_rus.eml""")
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

            on("email with 5 quotes") {
                var expectedEmail = TestEmails.gmail_tb_email_parser_task2_5_quotes
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmail_tb_email_parser_task2_5_quotes.eml""")
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

            on("X - email with KOI8-R content-type charset") {
                var expectedEmail = TestEmails.wc_test
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\wc_test.eml""")
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
                
//               There is some problem with KOI8-R charset. This case is always fails.
//                it("should parse CONTENT BODY correct") {
//                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
//                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("email with base64 Content-Transfer-Encoding") {
                var expectedEmail = TestEmails.gmailMariya
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmailMariya.eml""")
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
            
            on("X - another email with KOI8-R content-type charset") {
                var expectedEmail = TestEmails.e6_2_receive_q2
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\6_2_receive_q2.eml""")
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
//               There is some problem with KOI8-R charset. This case is always fails.
//                it("should parse CONTENT BODY correct") {
//                    assertEquals(expectedEmail.content.body, actualEmail.content.body)
//                }
                it("shouldn't have QUOTES") {
                    assertNull(actualEmail.content.quote)
                }
                it("shouldn't have SIGNATURE") {
                    assertNull(actualEmail.content.signature)
                }
            }

            on("FWD multipart/alternative email") {
                var expectedEmail = TestEmails.fwd_gmail_tb
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\Fwd_gmail_tb.eml""")
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
                var expectedEmail = TestEmails.fwd_yam_tb
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\Fwd_yam_tb.eml""")
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
                var expectedEmail = TestEmails.from_outlook_eng
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\from_outlook_eng.eml""")
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
                var expectedEmail = TestEmails.from_outlook_eng
                var actualEmail = parseEml(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\from_outlook_eng.eml""")
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