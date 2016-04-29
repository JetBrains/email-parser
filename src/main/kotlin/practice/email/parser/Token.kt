package practice.email.parser

import java.util.regex.Pattern

enum class TokenRegEx(val regex: String) {
    COMMA_END(",?"),
    DIGITS("\\d+" + COMMA_END.regex),
    DATE("[0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2}" + COMMA_END.regex),
    DATE_REVERSE("(?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]" + COMMA_END.regex),
    TIME("([01]?[0-9]|2[0-3]):[0-5][0-9]" + COMMA_END.regex),
    MERIDIEM("(A|a|P|p)\\.?(M|m)\\.?" + COMMA_END.regex),
    EMAIL("\\S+@\\S+\\.\\S+")
}

enum class AttributeRegEx(val regex: String) {
    ANGLE_BRACKETS("<.*>" + TokenRegEx.COMMA_END.regex),
    LAST_COMMA(".*,"),
    LAST_COLUMN(".*:"),
    HAS_AT(".*@.*"),
    NON_LETTER_OR_DIGIT("([!-/]|[:-@]|[\\[-`]|[{-~])+"),
    NON_ALPHABETIC("([0-9]|[!-/]|[:-@]|[\\[-`]|[{-~])+")
}

enum class TokenType {
    DEFAULT,
    DIGITS,
    DATE,
    TIME,
    MERIDIEM,
    EMAIL
}

class Token(val text: String) {

    private companion object Types {
        private val types: Array<Pair<TokenType, Array<TokenRegEx>>> = arrayOf(
                Pair(TokenType.DIGITS, arrayOf(TokenRegEx.DIGITS)),
                Pair(TokenType.DATE, arrayOf(TokenRegEx.DATE, TokenRegEx.DATE_REVERSE)),
                Pair(TokenType.TIME, arrayOf(TokenRegEx.TIME)),
                Pair(TokenType.MERIDIEM, arrayOf(TokenRegEx.MERIDIEM)),
                Pair(TokenType.EMAIL, arrayOf(TokenRegEx.EMAIL))
        )
    }

    var type = getTokenType()
    val attrs = getAttributes()

    inner class Attributes {
        var withAngleBrackets: Boolean
        var lastComma: Boolean
        var lastColumn: Boolean
        var hasAtSymbol: Boolean
        var nonLetterOrDigit: Boolean
        var nonAlphabetic: Boolean

        init {
            lastComma = check(AttributeRegEx.LAST_COMMA.regex)
            withAngleBrackets = check(AttributeRegEx.ANGLE_BRACKETS.regex)
            lastColumn = check(AttributeRegEx.LAST_COLUMN.regex)
            hasAtSymbol = check(AttributeRegEx.HAS_AT.regex)
            nonLetterOrDigit = check(AttributeRegEx.NON_LETTER_OR_DIGIT.regex)
            nonAlphabetic = check(AttributeRegEx.NON_ALPHABETIC.regex)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Attributes

            if (lastComma != other.lastComma) return false
            if (withAngleBrackets != other.withAngleBrackets) return false
            if (lastColumn != other.lastColumn) return false
            if (hasAtSymbol != other.hasAtSymbol) return false
            if (nonLetterOrDigit != other.nonLetterOrDigit) return false
            if (nonAlphabetic != other.nonAlphabetic) return false

            return true
        }

        override fun hashCode(): Int {
            var result = lastComma.hashCode()
            result += 31 * result + withAngleBrackets.hashCode()
            result += 31 * result + lastColumn.hashCode()
            result += 31 * result + hasAtSymbol.hashCode()
            result += 31 * result + nonLetterOrDigit.hashCode()
            result += 31 * result + nonAlphabetic.hashCode()
            return result
        }

    }

    fun check(regexp: String) = Pattern.matches(regexp, text)

    private fun getTokenType(): TokenType {
        types.forEach { pair ->
            pair.second.forEach { tokenRegEx ->
                if (check(tokenRegEx.regex)) {
                    return@getTokenType pair.first
                }
            }
        }
        return@getTokenType TokenType.DEFAULT
    }

    private fun getAttributes() = Attributes()

    fun getDifference(other: Token): Int {
        var res = 0;

        if (type != other.type)
            res += 1
        else {
            val lengthDiff = Math.abs(this.text.length - other.text.length)
            if (type == TokenType.DIGITS && lengthDiff > 0)
                res += 1
        }

        res += getAttributesDifference(other)

        return res
    }

    private fun getAttributesDifference(other: Token): Int {
        var res = 0

        if (attrs.lastComma != other.attrs.lastComma)
            res++

        if (attrs.withAngleBrackets != other.attrs.withAngleBrackets)
            res++

        if (attrs.lastColumn != other.attrs.lastColumn)
            res++

        if (attrs.hasAtSymbol != other.attrs.hasAtSymbol)
            res++

        if (attrs.nonLetterOrDigit != other.attrs.nonLetterOrDigit)
            res++

        if (attrs.nonAlphabetic != other.attrs.nonAlphabetic)
            res++

        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Token

        if (type != other.type) return false
        if (attrs != other.attrs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result += 31 * result + attrs.hashCode()
        return result
    }


}

