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

private val dir = ".${File.separator}testEmls${File.separator}AB${File.separator}"

class QuoteParserABSpecs : Spek({
  val parser = QuoteParser.Builder()
      .deleteQuoteMarks(false)
      .build()

  fun check(emailNum: Int, expectedQuoteHeader: QuoteHeader?) {
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
    val emailNum = 149
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 5,
        endIndex = 5,
        text = listOf()
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 4237
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 16,
        endIndex = 16,
        text = listOf()
    )
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 5370
    val expectedQuoteHeader = QuoteHeader(
        startIndex = 10,
        endIndex = 10,
        text = listOf()
    )
    check(emailNum, expectedQuoteHeader)
  }
})