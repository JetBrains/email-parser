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

/**
 * Represent header of the quote.
 * 
 * @property startIndex index of the string of the email message where header of the quote begins (inclusive)
 * @property endIndex index of the string of the email message where header of the quote ends (exclusive)
 * @property text header lines
 */
data class QuoteHeader(val startIndex: Int,
                       val endIndex: Int = startIndex,
                       val text: List<String>) {

    override fun toString(): String {
        return this.text.joinToString(separator = "\n")
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is QuoteHeader -> false
            this.startIndex != other.startIndex -> false
            this.endIndex != other.endIndex -> false
            this.text != other.text -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + startIndex
        result = prime * result + endIndex
        result = prime * result + text.hashCode()
        return result
    }
}