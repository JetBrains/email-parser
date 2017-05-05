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

class MiddleColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "MIDDLE_COLON"

    override fun getRegex(): Regex {
        // Full date regex for testing needs.
        @Language("RegExp")
        val regex = "[\\s\\p{C}\\p{Z}>]*\\S+[\\p{C}\\p{Z}\\s]*:[\\p{C}\\p{Z}\\s]+\\S+.*"

        // This regex matches the colon after any word except the last one with optional
        // whitespace before the colon and obligatory whitespace after the colon.
        return Regex("[\\s\\p{C}\\p{Z}>]*\\S+${this.whitespace}*:${this.whitespace}+\\S+.*")
    }
}

/**
 * If supposed header lines contains MiddleColonFeature most often
 * it is a multi line header.
 */
internal fun checkMiddleColonSuggestion(startIdx: Int, endIdx: Int, lines: List<String>,
                                       middleColonFeature: MiddleColonFeature): Boolean {
    return lines.subList(startIdx, endIdx + 1)
            .any { middleColonFeature.matches(it) }
}