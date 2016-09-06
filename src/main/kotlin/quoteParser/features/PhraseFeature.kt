package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
object KeyPhrases {
    @Language("Regexp")
    private val whitespace = "[\\p{C}\\p{Z}\\s]"

    val inReplyToRegex = "${this.whitespace}*>*${this.whitespace}*in${this.whitespace}+" +
            "reply${this.whitespace}+to:${this.whitespace}*"

    val replyAboveRegex = "${this.whitespace}*>*${this.whitespace}*##-.+-##${this.whitespace}*"

    val originalMsgRegex = "${this.whitespace}*-*${this.whitespace}*Original${this.whitespace}+" +
            "Message${this.whitespace}*-*${this.whitespace}*"

    val fwdMsgRegex = "${this.whitespace}*-*${this.whitespace}Forwarded${this.whitespace}+" +
            "message${this.whitespace}*-*${this.whitespace}*"

    val default = listOf<String>(
            inReplyToRegex,
            replyAboveRegex,
            originalMsgRegex
    )
}

class PhraseFeature(keyPhrases: List<String>) : AbstractQuoteFeature() {
    override val name: String
        get() = "PHRASE"

    private val commonRegex: String

    override fun getRegex(): Regex {
        return Regex(commonRegex, RegexOption.IGNORE_CASE)
    }

    init {
        commonRegex = keyPhrases.joinToString(prefix = "(", separator = ")|(", postfix = ")")
    }
}
