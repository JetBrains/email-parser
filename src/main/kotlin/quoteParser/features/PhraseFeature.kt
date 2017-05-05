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

/**
 * Contains some predefined key phrases.
 */
object KeyPhrases {
    @Language("Regexp")
    val whitespace = "[\\p{C}\\p{Z}\\s]"

    /**
     * "In reply to:" without match case and with possible leading '>'
     */
    val inReplyToRegex = "${this.whitespace}*>*${this.whitespace}*in${this.whitespace}+" +
            "reply${this.whitespace}+to:${this.whitespace}*"

    /**
     * "##- whatever text -##" without match case and with possible leading '>'
     */
    val replyAboveRegex = "${this.whitespace}*>*${this.whitespace}*##-.+-##${this.whitespace}*"

    /**
     * "Original Message" without match case and with possible leading and least '-' symbols
     */
    val originalMsgRegex = "${this.whitespace}*-*${this.whitespace}*Original${this.whitespace}+" +
            "Message${this.whitespace}*-*${this.whitespace}*"

    /**
     * "Forwarded message" without match case and with possible leading and least '-' symbols
     */
    val fwdMsgRegex = "${this.whitespace}*-*${this.whitespace}Forwarded${this.whitespace}+" +
            "message${this.whitespace}*-*${this.whitespace}*"

    /**
     * List of key phrases containing [inReplyToRegex], [replyAboveRegex], [originalMsgRegex] regexes.
     */
    val default = listOf<String>(
            this.inReplyToRegex,
            this.replyAboveRegex,
            this.originalMsgRegex
    )
}

class PhraseFeature(keyPhrases: List<String>) : AbstractQuoteFeature() {
    override val name: String
        get() = "PHRASE"

    private val commonRegex: String

    override fun getRegex(): Regex {
        return Regex(this.commonRegex, RegexOption.IGNORE_CASE)
    }

    init {
        this.commonRegex = keyPhrases.joinToString(prefix = "(", separator = ")|(", postfix = ")")
    }
}
