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
        val inReplyToRegex = "\\s*in[\\s\\xA0]+reply[\\s\\xA0]+to:\\s*"
        @Language("RegExp")
        val replyAboveRegex = "\\s*##-.+-##\\s*"
        
        val wholeRegex = listOf<String>(
                inReplyToRegex, 
                replyAboveRegex
        ).joinToString(prefix = "(", separator = ")|(", postfix = ")") 
        
        return Regex(wholeRegex, RegexOption.IGNORE_CASE)
    }
}
