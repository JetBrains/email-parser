package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class EmailFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "EMAIL"
    override fun getRegex(): Regex {
        
        // full regex fo testing needs
//        @Language("RegExp")
//        val regex = "(.*[\\s\\p{C}\\p{Z}])?\\S+@\\S+([\\p{C}\\p{Z}\\s].*)?"
        
        return Regex("${startSpaceOptional}\\S+@\\S+${endSpaceOptional}")
    }

}