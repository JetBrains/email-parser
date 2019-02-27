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

private val dir = ".${File.separator}testEmls${File.separator}empty${File.separator}"

class QuoteParserEmptySpecs : Spek({
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
    val emailNum = 93
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 102
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 146
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 205
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 1165
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 3505
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }

  given("eml file") {
    val emailNum = 20454
    val expectedQuoteHeader = null
    check(emailNum, expectedQuoteHeader)
  }
})