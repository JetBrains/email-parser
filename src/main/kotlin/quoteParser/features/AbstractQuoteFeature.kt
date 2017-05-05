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

abstract class AbstractQuoteFeature() {
    @Language("Regexp")
    protected val whitespace = "[\\p{C}\\p{Z}\\s]"
    @Language("Regexp")
    protected val startWhitespaceOptional = "(.*[\\s\\p{C}\\p{Z}>])?"
    @Language("Regexp")
    protected val endWhitespaceOptional = "([\\p{C}\\p{Z}\\s].*)?"
    @Language("Regexp")
    protected val startBracketsOptional = "\\p{C}*[\\.,\\{\\[<\\*\\(:\"'`\\|\\\\/~]?\\p{C}*"
    @Language("Regexp")
    protected val endBracketsOptional = "\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*"

    abstract val name: String

    abstract protected fun getRegex(): Regex

    open fun matches(line: String): Boolean = this.getRegex().matches(line)
}