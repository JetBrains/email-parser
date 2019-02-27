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

package quoteParser.features

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuoteMarkFeatureSpecs : Spek({
  val quoteMarkFeature = QuoteMarkFeature()

  given("standalone gt symbol") {
    val s = ">"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("several > symbols") {
    val s = ">>>"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("several > symbols with whitespaces") {
    val s = "\t\t >  > >"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given(">text") {
    val s = ">text"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given(">>>text") {
    val s = ">>>text"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("\t >\t >  >\t\ttext") {
    val s = ">>>text"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("text with non-breaking whitespaces") {
    val s = " > > > text"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("text with non-breaking whitespaces and invisible(\\u200e) characters") {
    val s = " ‎>‎ > ‎‎>‎ text"

    on("checking regexes") {
      it("should match GREATER_THAN regex") {
        assertTrue { quoteMarkFeature.matches(s) }
      }
    }
  }
  given("text without leading >") {
    val s = "text > text"

    on("checking regexes") {
      it("should not match GREATER_THAN regex") {
        assertFalse { quoteMarkFeature.matches(s) }
      }
    }
  }

  given("several text lines") {
    val lines = listOf<String>(
        "text > text",
        ">   ",
        ">text",
        "\t> TEXT"
    )
    on("calling matchLines function") {
      val matching = quoteMarkFeature.matchLines(lines)
      it("must get appropriate value") {
        assertTrue { matching[0] == QuoteMarkMatchingResult.NOT_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[1] == QuoteMarkMatchingResult.V_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[2] == QuoteMarkMatchingResult.V_NOT_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[3] == QuoteMarkMatchingResult.V_NOT_EMPTY }
      }
    }
  }

  given("several text lines") {
    val lines = listOf<String>(
        ">text > text",
        " \t  ",
        "\t>  > TEXT",
        "\t>  ",
        "\t TEXT"
    )
    on("calling matchLines function") {
      val matching = quoteMarkFeature.matchLines(lines)
      it("must get appropriate value") {
        assertTrue { matching[0] == QuoteMarkMatchingResult.V_NOT_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[1] == QuoteMarkMatchingResult.EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[2] == QuoteMarkMatchingResult.V_NOT_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[3] == QuoteMarkMatchingResult.V_EMPTY }
      }
      it("must get appropriate value") {
        assertTrue { matching[4] == QuoteMarkMatchingResult.NOT_EMPTY }
      }
    }
  }
})