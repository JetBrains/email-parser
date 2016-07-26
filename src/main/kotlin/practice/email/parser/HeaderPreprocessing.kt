package practice.email.parser

import java.util.regex.Pattern

fun preprocess(lines: List<String>): String {
    var modifLines = deleteStartingAngleBrackets(lines)
    modifLines = deletePeriodsAndCommas(modifLines)
    modifLines = joinAngleBracketsAndColons(modifLines)

    return modifLines.filter { it != "" }.joinToString(separator = " ")
}

private fun joinAngleBracketsAndColons(lines: List<String>): List<String>  {
    var modifLines = joinOpenAngleBrackets(lines)
    modifLines = joinCloseAngleBracketsAndColons(modifLines)
    return modifLines
}

private fun joinOpenAngleBrackets(lines: List<String>): List<String> {
    var addToStart = false
    var wordToAdd = "some init val"
    val modifLines = lines.mapIndexed { lineIdx, line ->
        val words = getWords(line.trim()).toMutableList()
        if (addToStart) {
            words.add(0, wordToAdd)
            addToStart = false
        }
        var i = 0
        while (i < words.size) {
            val word = words[i]
            if (word == "<" || word == "<<") {
                if (i < words.size - 1) {
                    words.removeAt(i)
                    words[i] = "${word}${words[i]}"
                    --i
                } else if (i == words.size - 1 && lineIdx < lines.size - 1) {
                    words.removeAt(i)
                    addToStart = true
                    wordToAdd = word
                    --i
                }
            }
            i++
        }

        words.joinToString(separator = " ")
    }

    return modifLines
}

private fun joinCloseAngleBracketsAndColons(lines: List<String>): List<String> {
    var addToEnd = false
    var wordToAdd = "some init val"
    val linesReversed = lines.reversed()
    val modifLines = linesReversed.mapIndexed { lineIdx, line ->
        val words = getWords(line.trim()).toMutableList()
        if (addToEnd) {
            words.add(wordToAdd)
            addToEnd = false
        }
        var i = words.size - 1
        while (i >= 0) {
            val word = words[i]
            if (word == ">" || word == ">>" ||word == ">:" || word == ">>:" || word == ":") {
                if (i > 0) {
                    words.removeAt(i)
                    words[i-1] = "${words[i-1]}${word}"
                } else if (i == 0 && lineIdx < linesReversed.size - 1) {
                    words.removeAt(i)
                    addToEnd = true
                    wordToAdd = word
                }
            }
            i--
        }

        words.joinToString(separator = " ")
    }

    return modifLines.reversed()
}

private fun deleteStartingAngleBrackets(lines: List<String>): List<String> =
        lines.map {
            val line = it.trim()
            var words = getWords(line).toMutableList()

            if (words[0].equals(">")) {
                words = words.subList(1, words.size)
            } else if (words[0].startsWith('>')) {
                words[0] = words[0].substring(1, words[0].length)
            }

            words.joinToString(separator = " ")
        }

private fun deletePeriodsAndCommas(lines: List<String>): List<String> =
        lines.map {
            val line = it.trim()
            var words = getWords(line)

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

private fun lastPeriodOrComma(word: String): Boolean =
        word.length > 0 && (word[word.length - 1] == '.' || word[word.length - 1] == ',')

private fun getWords(line: String) =
        line.split(Pattern.compile("\\s")).filter { it != "" }

