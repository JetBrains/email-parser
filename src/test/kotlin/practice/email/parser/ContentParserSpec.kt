package practice.email.parser

import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ContentParserSpec : Spek() {
    init {
        given("email content parser") {
            on("simple email with just body and both parseMode - DEEP and ONE") {
                val expectesContent = TestContents.yam_tb_eng_rus
                var actualContent = parseEmlWithContent(
                    File("""D:\IDEA workspace\email-parser\src\test\resources\yam_tb_eng_rus.eml"""),
                    EmailContentParseMode.ONE
                ).content

                it("should parse body correct - mode ONE") {
                    assertEquals(expectesContent.body, actualContent.body)
                }
                it("should contain null quote field - mode ONE") {
                    assertNull(actualContent.quote)
                }
                it("should contain null signature field - mode ONE") {
                    assertNull(actualContent.signature)
                }

                actualContent = parseEmlWithContent(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\yam_tb_eng_rus.eml"""),
                        EmailContentParseMode.DEEP
                ).content

                it("should parse body correct - mode DEEP") {
                    assertEquals(expectesContent.body, actualContent.body)
                }
                it("should contain null quote field - mode DEEP") {
                    assertNull(actualContent.quote)
                }
                it("should contain null signature field - mode DEEP") {
                    assertNull(actualContent.signature)
                }
            }

            on("email with body and signature on both parseMode - DEEP and ONE") {
                val expectesContent = TestContents.gmailMariya
                var actualContent = parseEmlWithContent(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmailMariya.eml"""),
                        EmailContentParseMode.ONE
                ).content

                it("should parse body correct - ONE") {
                    assertEquals(expectesContent.body, actualContent.body)
                }
                it("should contain null quote field - ONE") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature correct - ONE") {
                    assertEquals(expectesContent.signature, actualContent.signature)
                }

                actualContent = parseEmlWithContent(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmailMariya.eml"""),
                        EmailContentParseMode.DEEP
                ).content

                it("should parse body correct - DEEP") {
                    assertEquals(expectesContent.body, actualContent.body)
                }
                it("should contain null quote field - DEEP") {
                    assertNull(actualContent.quote)
                }
                it("should parse signature correct - DEEP") {
                    assertEquals(expectesContent.signature, actualContent.signature)
                }
            }

            on("email with many nested quotes for parseMode - ONE") {
                val expectesContent = TestContents.ONE_gmail_tb_email_parser_task2_5_quotes
                val actualContent = parseEmlWithContent(
                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmail_tb_email_parser_task2_5_quotes.eml"""),
                        EmailContentParseMode.ONE
                ).content

                it("should parse body correct") {
                    assertEquals(expectesContent.body, actualContent.body)
                }
                it("should parse quote correct") {
                    assertEquals(expectesContent.quote, actualContent.quote)
                }
// TODO explore what wrong with this test
//                it("should contain null signature field") {
//                    assertEquals(expectesContent.signature, actualContent.signature)
//                }
            }

// TODO Make test for testing recursive quotes (and both quotes and singns in one email)
//            on("email with many nested quotes for parseMode - DEEP") {
//                val expectesContent = TestContents.DEEP_gmail_tb_email_parser_task2_5_quotes
//                val actualContent = parseEmlWithContent(
//                        File("""D:\IDEA workspace\email-parser\src\test\resources\gmail_tb_email_parser_task2_5_quotes.eml"""),
//                        EmailContentParseMode.DEEP
//                ).content
//                
//            }
        }
    }
}