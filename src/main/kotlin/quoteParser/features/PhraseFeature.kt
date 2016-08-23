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
        val inReplyToRegex = "[\\s\\p{C}\\p{Z}]*in[\\s\\p{C}\\p{Z}]+reply[\\s\\p{C}\\p{Z}]+to:[\\s\\p{C}\\p{Z}]*"
        @Language("RegExp")
        val replyAboveRegex = "[\\s\\p{C}\\p{Z}]*##-.+-##[\\s\\p{C}\\p{Z}]*"
        
        return Regex("($inReplyToRegex)|($replyAboveRegex)", RegexOption.IGNORE_CASE)
    }
}
