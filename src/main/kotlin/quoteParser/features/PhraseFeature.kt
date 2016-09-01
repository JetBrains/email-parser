package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class PhraseFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "PHRASE"

    override fun getRegex(): Regex {
        val inReplyToRegex = "${this.whitespace}*>*${this.whitespace}*in${this.whitespace}+" +
                "reply${this.whitespace}+to:${this.whitespace}*"
        val replyAboveRegex = "${this.whitespace}*>*${this.whitespace}*##-.+-##${this.whitespace}*"
        val originalMsgRegex = "${this.whitespace}*-*${this.whitespace}*Original${this.whitespace}+" +
                "Message${this.whitespace}*-*${this.whitespace}*"
        val fwdMsgRegex = "${this.whitespace}*-*${this.whitespace}Forwarded${this.whitespace}+" +
                "message${this.whitespace}*-*${this.whitespace}*"

        return Regex("($inReplyToRegex)|($replyAboveRegex)|($originalMsgRegex)", RegexOption.IGNORE_CASE)
    }
}
