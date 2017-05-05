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
 * Email message with separate body, header of the quote
 * and citation itself.
 * 
 * @property body useful content of the message
 * @property header header of the quote if exists
 * @property quote the rest part of the message if exists
 */
data class Content(val body: List<String>,
                   val header: QuoteHeader?,
                   val quote: Content?) {

    /**
     * Get string representation of Content object.
     * 
     * @param addMarks if set to true adds '>' from the start of the header of the quote till the end of the message
     * @param uppercaseHeader if set to true then converts header to uppercase 
     * @return string representation of the object
     */
    fun toString(addMarks: Boolean = true, uppercaseHeader: Boolean = false): String {
        val prefix = if (addMarks) "> " else ""
        val separator = if (addMarks) "\n> " else "\n"

        val bodyText = if (!this.body.isEmpty()) {
            this.body.joinToString(
                    separator = "\n",
                    postfix = if (this.header != null) "\n" else ""
            )
        } else {
            ""
        }

        val headerText = if (this.header != null && !this.header.text.isEmpty())
            this.header.text
                    .joinToString(prefix = prefix, separator = separator, postfix = "\n") {
                        if (uppercaseHeader) it.toUpperCase() else it
                    }
        else
            ""

        val quoteText = if (this.quote != null && !this.quote.body.isEmpty())
                this.quote
                        .toString(addMarks, uppercaseHeader).lines()
                        .joinToString(prefix = prefix, separator = separator, postfix = "")
        else
            ""

        return StringBuilder(bodyText)
                .append(headerText)
                .append(quoteText)
                .toString()
    }

    override fun toString(): String {
        return toString(addMarks = true, uppercaseHeader = false)
    }
}