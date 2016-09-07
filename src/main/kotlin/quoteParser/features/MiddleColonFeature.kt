package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class MiddleColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "MIDDLE_COLON"

    override fun getRegex(): Regex {
        // Full date regex for testing needs.
        @Language("RegExp")
        val regex = "[\\s\\p{C}\\p{Z}>]*\\S+[\\p{C}\\p{Z}\\s]*:[\\p{C}\\p{Z}\\s]+\\S+.*"

        // This regex matches the colon after any word except the last one with optional
        // whitespace before the colon and obligatory whitespace after the colon.
        return Regex("[\\s\\p{C}\\p{Z}>]*\\S+${this.whitespace}*:${this.whitespace}+\\S+.*")
    }
}

/**
 * If supposed header lines contains MiddleColonFeature most often
 * it is a multi line header.
 */
internal fun checkMiddleColonSuggestion(startIdx: Int, endIdx: Int, lines: List<String>,
                                       middleColonFeature: MiddleColonFeature): Boolean {
    return lines.subList(startIdx, endIdx + 1)
            .any { middleColonFeature.matches(it) }
}