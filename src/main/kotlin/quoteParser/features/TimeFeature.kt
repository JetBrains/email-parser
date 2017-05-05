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

class TimeFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "TIME"

    override fun getRegex(): Regex {
        @Language("RegExp")
        val hhmm = "([01]?[0-9]|2[0-3])\\p{C}*:\\p{C}*([0-5][0-9])"

        @Language("RegExp")
        val sec = "\\p{C}*:\\p{C}*[0-5][0-9]"

        @Language("RegExp")
        val ampm = "\\p{C}*[aApP]\\p{C}*[,\\.]{0,2}\\p{C}*[mM]\\p{C}*[,\\.]{0,2}"

        // Time regex with optional seconds and meridian. Could be surrounded with brackets. 
        val time = "${this.startWhitespaceOptional}(${this.startBracketsOptional}" +
                "($hhmm)($sec)?($ampm)?" +
                "${this.endBracketsOptional})${this.endWhitespaceOptional}"

        // Full time regex for testing needs.
        @Language("RegExp")
        val regex = "(.*[\\s\\p{C}\\p{Z}>])?(\\p{C}*[\\.,\\{\\[<\\*\\(:\"'`\\|\\\\/~]?\\p{C}*(([01]?[0-9]|2[0-3])\\p{C}*:\\p{C}*([0-5][0-9]))(\\p{C}*:\\p{C}*[0-5][0-9])?(\\p{C}*[aApP]\\p{C}*[,\\.]{0,2}\\p{C}*[mM]\\p{C}*[,\\.]{0,2})?\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*)([\\s\\p{C}\\p{Z}].*)?"


        return Regex(time)
    }
}
