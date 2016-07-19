package practice.email.parser

import java.util.regex.Pattern

fun preprocess(lines: List<String>): String {
    var modifLines = deleteStartingAngleBrackets(lines)
    modifLines = deletePeriodsAndCommas(modifLines)

/*
    modifLines = joinEmailAndAngleBrackets(modifLines)
    modifLines = joinColonsWithPreviousWord(modifLines)
*/
    return modifLines.joinToString(separator = " ")
}
/*

fun joinColonsWithPreviousWord(modifLines: List<String>): List<String> {

}

fun joinEmailAndAngleBrackets(modifLines: List<String>): List<String> {

}
*/

fun deleteStartingAngleBrackets(lines: List<String>): List<String> =
        lines.map {
            val line = it.trim()
            var words = line.split(Pattern.compile("\\s"))

            if (words[0].equals(">")) {
                words = words.subList(1, words.size)
            }

            words.joinToString(separator = " ")
        }

fun deletePeriodsAndCommas(lines: List<String>): List<String> =
        lines.map {
            val line = it.trim()
            var words = line.split(Pattern.compile("\\s"))

            words = words.map {
                var word: String = it
                if (lastPeriodOrComma(word)) {

                    word = word.substring(0, word.length - 1)
                    if (lastPeriodOrComma(word)) {
                        word = word.substring(0, word.length - 1)
                    }
                }
                word
            }

            words.joinToString(separator = " ")
        }

fun lastPeriodOrComma(word: String): Boolean =
        word.length > 0 && (word[word.length - 1] == '.' || word[word.length - 1] == ',')

