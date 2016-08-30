package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class PhraseFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "PHRASE"
    override fun getRegex(): Regex {
        @Language("RegExp")
        val inReplyToRegex = "${this.whitespace}*>*${this.whitespace}*in${this.whitespace}+reply${this.whitespace}+to:${this.whitespace}*"
        @Language("RegExp")
        val replyAboveRegex = "${this.whitespace}*>*${this.whitespace}*##-.+-##${this.whitespace}*"

        return Regex("($inReplyToRegex)|($replyAboveRegex)", RegexOption.IGNORE_CASE)
    }
}
