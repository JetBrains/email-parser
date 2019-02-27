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

private val dir = ".${File.separator}testEmls${File.separator}SL${File.separator}"

class QuoteParserSLSpecs : Spek({
  val parser = QuoteParser.Builder()
      .deleteQuoteMarks(false)
      .build()

  fun check(emailNum: Int, expectedQuoteHeader: QuoteHeader) {
    val url = parser.javaClass.classLoader.getResource("$dir$emailNum.eml")
    val file = File(url.toURI())
    on("processing it") {
      val content = parser.parse(file)
      it("must define a quote") {
        assertEquals(expectedQuoteHeader, content.header)
      }
    }
  }

  given("eml file") {
    val emailNum = 10
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 11,
        endIndex = 12,
        text = listOf(
            "On 26 May 2014, at 14:25 , Robin <robin@man.com> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 20
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 5,
        endIndex = 6,
        text = listOf(
            "2014-05-23 16:32 GMT+04:00 catwoman catwoman <cat@woman.com>:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 26
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 18,
        endIndex = 20,
        text = listOf(
            "2014-05-27 13:02 GMT+02:00 WHO IS <",
            "who@is.com>:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 75
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 7,
        endIndex = 8,
        text = listOf(
            "On May 29, 2014 at 10:01:16 AM, Xxxx Yyyy (asdasd@asdadsasd.cd) wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 108
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 13,
        endIndex = 14,
        text = listOf(
            "On 12/05/2014 13:00, Professor X wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 187
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 4,
        endIndex = 5,
        text = listOf(
            "On 05.июня.2014, at 14:50, Censored Cen <dd-dd@dd.com> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 214
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 10,
        endIndex = 12,
        text = listOf(
            "On Friday, June 6, 2014, Eddard Stark <strak.rulez@winteriscoming.com>",
            "wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 245
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 33,
        endIndex = 34,
        text = listOf(
            "On 02/06/2014 11:34, Wonder Woman wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 259
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 35,
        endIndex = 37,
        text = listOf(
            "> Capitan America <mailto:America.Capitan@avengers.com>",
            "> 10 Jun 2014 17:49"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 374
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 4,
        endIndex = 5,
        text = listOf(
            "Am Dienstag, 17. Juni 2014 schrieb Xxx Yyyy :"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 427
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 8,
        endIndex = 9,
        text = listOf(
            """-Original Message-On 20/06/2014 11:40 AM, "Xxxxx Yyyyy" <sdf-sdf@sdf.com""" +
                """<mailto:sdf-sdf@sdf.com>> wrote:"""
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 484
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 2,
        endIndex = 4,
        text = listOf(
            """On Jun 25, 2014 5:51 PM, "Harry Potter" <ads-asd@asd.com>""",
            "wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 599
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 12,
        endIndex = 13,
        text = listOf(
            """Am 30.06.2014 16:31, schrieb XXX YYY:"""
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 704
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 7,
        endIndex = 8,
        text = listOf(
            "Dnia 3 lip 2014 o godz. 19:33 text text <text-text@text.com> napisał(a):"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 800
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 12,
        endIndex = 14,
        text = listOf(
            "2014-07-10 12:13 GMT+02:00 asd asd <a.asd@a.com>",
            ":"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 880
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 16,
        endIndex = 17,
        text = listOf(
            "On Mon, Jul 14, 2014 at 11:11 AM, Dan Banan <dan@banan.com> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 967
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 3,
        endIndex = 5,
        text = listOf(
            "On Fri, Jul 18, 2014 at 1:17 AM, Dart Vader <luke@imyourfather.com",
            "> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 1066
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 19,
        endIndex = 20,
        text = listOf(
            "On 7/11/14, 9:30 AM, X Y wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 1077
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 2,
        endIndex = 3,
        text = listOf(
            "суббота, 26 июля 2014 г. пользователь Some Body написал:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 3711
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 10,
        endIndex = 11,
        text = listOf(
            "---- On Mon, 17 Nov 2014 13:31:52 -0800 Text Text &lt;Text.Text@Text.com&gt; wrote ---- "
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 3997
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 9,
        endIndex = 10,
        text = listOf(
            "Op 25 nov. 2014 om 18:44 heeft text text <text-text@text.com<mailto:text-text@text.com>>" +
                " het volgende geschreven:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 4008
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 25,
        endIndex = 26,
        text = listOf(
            "Wiadomość napisana przez text text <text-text@text.com> w dniu 25 lis 2014, o godz. 21:44:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 4169
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 40,
        endIndex = 40,
        text = listOf()
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 11461
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 25,
        endIndex = 27,
        text = listOf(
            "text text <text.text@text.com> schrieb am Sa., 19. Sep.",
            "2015 um 12:04 Uhr:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 17444
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 18,
        endIndex = 19,
        text = listOf(
            ">>> text text <text-text-text@text.com> 10/22/15 21:02 >>>"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 17949
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 37,
        endIndex = 38,
        text = listOf(
            "On Nov 2, 2015, at 10:09 AM, text text (text text) <textck.text@text.com<mailto:" +
                "text.text@text.com>> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 17995
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 2,
        endIndex = 3,
        text = listOf(
            "> 5 нояб. 2015 г., в 13:37, test test (test test) <test.test@test.com> написал(а):"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 18075
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 19,
        endIndex = 21,
        text = listOf(
            "Il giorno ven 6 nov 2015 alle ore 12:30 text text (text text) <",
            "text.text@text.com> ha scritto:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 18762
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 10,
        endIndex = 11,
        text = listOf(
            "     On Wednesday, November 11, 2015 2:51 PM, text text (text textack) <text.text@asd." +
                "com> wrote:"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 21086
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 3,
        endIndex = 4,
        text = listOf(
            "On Tue, 15 Mar 2016 12:30:19 +0000"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 21593
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 1,
        endIndex = 4,
        text = listOf(
            "> 在 2016年4月15日，17:14，text text (text text) <text-text-text@text.com> 写道：",
            "> ",
            "> ##- Please type your reply above this line -##"
        )
    )
    check(emailNum, expectedQuoteHeader)
  }
})