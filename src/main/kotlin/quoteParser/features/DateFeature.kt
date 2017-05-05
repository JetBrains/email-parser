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

import org.intellij.lang.annotations.Language

class DateFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "DATE"

    override fun getRegex(): Regex {
        // Short date format starting with the day number (e.g. 15-02-2016)
        @Language("RegExp")
        val shortDateForward = "[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2}"
        // Short date format starting with the year (2016-02-15)
        @Language("RegExp")
        val shortDateReversed = "(20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]"
        val shortDate = "(($shortDateForward)|($shortDateReversed))\\p{C}*[,\\.]{0,2}"

        // Full date format with up to three possible arbitrary words 
        // between number of the day and year. Number of the day goes first
        // (e.g. 15 Feb 2016; 15 Thu, Feb 2016, 15 x y z 2016).
        val fullDateForward = "([0-3]?[0-9]\\p{C}*[\\.,]{0,2}${this.whitespace}+)" +
                "(\\S+${this.whitespace}+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2})"

        // The same as previous but year and number of the day are swapped.
        val fullDateReversed = "(20\\d\\d\\p{C}*[\\.,]{0,2}${this.whitespace}+)" +
                "(\\S+${this.whitespace}+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2})"
        val fullDate = "($fullDateForward)|($fullDateReversed)"

        // Final date regex. Colon(:) is possible before the date, 
        // arbitrary bracket is possible after the date.
        val date = "(.*[\\p{C}\\p{Z}\\s:>])?(($shortDate)|($fullDate))${this.endBracketsOptional}" +
                "${this.endWhitespaceOptional}"

        // Full date regex for testing needs.
        @Language("RegExp")
        val regex = "(.*[\\p{C}\\p{Z}\\s:>])?(((([0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2})|((20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]))\\p{C}*[,\\.]{0,2})|((([0-3]?[0-9]\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2}))|((20\\d\\d\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2}))))\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*([\\p{C}\\p{Z}\\s].*)?"

        return Regex(date)
    }

}
