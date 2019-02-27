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

package quoteParser

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import kotlin.test.assertEquals

private val dir = ".${File.separator}testEmls${File.separator}recursive${File.separator}"

class QuoteParserRecSpecs : Spek({
  val parser = QuoteParser.Builder()
      .deleteQuoteMarks(true)
      .recursive(true)
      .build()

  given("eml file") {
    val emailNum = 270
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 4,
        endIndex = 6,
        text = listOf(
            """On Tue, Jun 3, 2014 at 11:17 AM, asd asd <""",
            """asd-asd@asd.com> wrote:"""
        )
    )
    val expectedInnerQuoteHeader = QuoteHeader(
        startIndex = 4,
        endIndex = 5,
        text = listOf(
            """In reply to:"""
        )
    )
    val url = parser.javaClass.classLoader.getResource("$dir$emailNum.eml")
    val file = File(url.toURI())
    on("processing it") {
      val content = parser.parse(file)
      it("must define a quote") {
        assertEquals(expectedQuoteHeader, content.header)
      }
      it("must define an inner quote") {
        assertEquals(expectedInnerQuoteHeader, content.quote?.header)
      }
    }
  }

  given("eml file") {
    val emailNum = 6510
    val expectedQuoteHeader1 = QuoteHeader(
        startIndex = 2,
        endIndex = 3,
        text = listOf(
            """-----Original Message-----"""
        )
    )
    val expectedQuoteHeader2 = QuoteHeader(
        startIndex = 0,
        endIndex = 4,
        text = listOf(
            """From: "text text (text)" <text.text@text.com>""",
            """Sent: Friday, February 13, 2015, 6:44:58 PM""",
            """To: """,
            """Subject: [text] Update: [text-text text] text-123456"""
        )
    )
    val expectedQuoteHeader3 = QuoteHeader(
        startIndex = 4,
        endIndex = 5,
        text = listOf(
            """##- Please type your reply above this line -##  """
        )
    )
    val url = parser.javaClass.classLoader.getResource("$dir$emailNum.eml")
    val file = File(url.toURI())
    on("processing it") {
      val content = parser.parse(file)
      it("must define an outer quote") {
        assertEquals(expectedQuoteHeader1, content.header)
      }
      it("must define inner1 quote") {
        assertEquals(expectedQuoteHeader2, content.quote?.header)
      }
      it("must define inner2 quote") {
        assertEquals(expectedQuoteHeader3, content.quote?.quote?.header)
      }
    }
  }
})