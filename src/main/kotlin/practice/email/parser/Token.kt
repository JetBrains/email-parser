package practice.email.parser

import java.util.regex.Pattern

enum class TokenRegEx(val regex: String) {
    COMMA_END(",?"),
    DIGITS("\\d+" + COMMA_END.regex),
    DATE("(([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" +
         "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))"  + COMMA_END.regex),
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
        private val types: Array<Pair<TokenType, TokenRegEx>> = arrayOf(
                Pair(TokenType.DIGITS, TokenRegEx.DIGITS),
                Pair(TokenType.DATE, TokenRegEx.DATE),
                Pair(TokenType.TIME, TokenRegEx.TIME),
                Pair(TokenType.MERIDIEM, TokenRegEx.MERIDIEM),
                Pair(TokenType.EMAIL, TokenRegEx.EMAIL) 
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
            this.lastComma = check(AttributeRegEx.LAST_COMMA.regex)
            this.withAngleBrackets = check(AttributeRegEx.ANGLE_BRACKETS.regex)
            this.lastColumn = check(AttributeRegEx.LAST_COLUMN.regex)
            this.hasAtSymbol = check(AttributeRegEx.HAS_AT.regex)
            this.nonLetterOrDigit = check(AttributeRegEx.NON_LETTER_OR_DIGIT.regex)
            this.nonAlphabetic = check(AttributeRegEx.NON_ALPHABETIC.regex)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Attributes

            if (this.lastComma != other.lastComma) return false
            if (this.withAngleBrackets != other.withAngleBrackets) return false
            if (this.lastColumn != other.lastColumn) return false
            if (this.hasAtSymbol != other.hasAtSymbol) return false
            if (this.nonLetterOrDigit != other.nonLetterOrDigit) return false
            if (this.nonAlphabetic != other.nonAlphabetic) return false

            return true
        }

        override fun hashCode(): Int {
            var result = this.lastComma.hashCode()
            result += 31 * result + this.withAngleBrackets.hashCode()
            result += 31 * result + this.lastColumn.hashCode()
            result += 31 * result + this.hasAtSymbol.hashCode()
            result += 31 * result + this.nonLetterOrDigit.hashCode()
            result += 31 * result + this.nonAlphabetic.hashCode()
            return result
        }

    }

    fun check(regexp: String) = Pattern.matches(regexp, text)

    private fun getTokenType(): TokenType {
        Types.types.forEach { pair ->
            if (check(pair.second.regex)) {
                return@getTokenType pair.first
            }
        }
        return TokenType.DEFAULT
    }

    private fun getAttributes() = Attributes()

    fun getDifference(other: Token): Int {
        var difference = 0;

        if (this.type != other.type)
            difference += 1
        else {
            val lengthDiff = Math.abs(this.text.length - other.text.length)
            if (this.type == TokenType.DIGITS && lengthDiff > 0)
                difference += 1
        }

        difference += getAttributesDifference(other)

        return difference
    }

    private fun getAttributesDifference(other: Token): Int {
        var difference = 0

        if (this.attrs.lastComma != other.attrs.lastComma)
            difference++

        if (this.attrs.withAngleBrackets != other.attrs.withAngleBrackets)
            difference++

        if (this.attrs.lastColumn != other.attrs.lastColumn)
            difference++

        if (this.attrs.hasAtSymbol != other.attrs.hasAtSymbol)
            difference++

        if (this.attrs.nonLetterOrDigit != other.attrs.nonLetterOrDigit)
            difference++

        if (this.attrs.nonAlphabetic != other.attrs.nonAlphabetic)
            difference++

        return difference
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Token

        if (this.type != other.type) return false
        if (this.attrs != other.attrs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.type.hashCode()
        result += 31 * result + this.attrs.hashCode()
        return result
    }


}

