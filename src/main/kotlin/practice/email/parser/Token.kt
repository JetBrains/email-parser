package practice.email.parser

import java.util.regex.Pattern

enum class TokenRegEx(val regex: String) {
    COMMA_END(",?"),
    DIGITS("\\d+" + COMMA_END.regex),
    DATE("(([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" +
            "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))" + COMMA_END.regex),
    TIME("([01]?[0-9]|2[0-3]):[0-5][0-9]" + COMMA_END.regex),
    MERIDIEM("(A|a|P|p)\\.?(M|m)\\.?" + COMMA_END.regex),
    EMAIL("\\S+@\\S+")
}

enum class AttributeRegEx(val regex: String) {
    ANGLE_BRACKETS("<.*>" + TokenRegEx.COMMA_END.regex),
    LAST_COMMA(".*,"),
    LAST_COLUMN(".*:"),
    HAS_AT(".*@.*"),
    NON_ALPHABETIC("([0-9]|[!-/]|[:-@]|[\\[-`]|[{-~])+"),
    NON_ALPHABETIC_OR_DIGIT("([!-/]|[:-@]|[\\[-`]|[{-~])+")
}

enum class TokenType {
    UNDEFINED,
    DIGITS,
    DATE,
    TIME,
    MERIDIEM,
    EMAIL
}

class Token(var text: String) {

    companion object Static {
        val INSERTION_COST = mapOf<TokenType, Int>(
                Pair(TokenType.UNDEFINED, 10),
                Pair(TokenType.DIGITS, 10),
                Pair(TokenType.DATE, 10),
                Pair(TokenType.TIME, 10),
                Pair(TokenType.MERIDIEM, 10),
                Pair(TokenType.EMAIL, 50)
        )
        val REPLACEMENT_COST = arrayOf(
                intArrayOf(0, 10, 10, 10, 10, 50),
                intArrayOf(10, 0, 10, 10, 10, 50),
                intArrayOf(10, 10, 0, 10, 10, 50),
                intArrayOf(10, 10, 10, 0, 10, 50),
                intArrayOf(10, 10, 10, 10, 0, 50),
                intArrayOf(50, 50, 50, 50, 50, 0)
        )
        val DIGITS_INEQUALITY_COST = 10
        val ATTRIBUTE_INEQUALITY_COST = 1

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
            this.nonLetterOrDigit = check(AttributeRegEx.NON_ALPHABETIC_OR_DIGIT.regex)
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
            result += 13 * result + this.withAngleBrackets.hashCode()
            result += 13 * result + this.lastColumn.hashCode()
            result += 13 * result + this.hasAtSymbol.hashCode()
            result += 13 * result + this.nonLetterOrDigit.hashCode()
            result += 13 * result + this.nonAlphabetic.hashCode()
            return result
        }

        override fun toString(): String {
            return "Attributes(withAngleBrackets=$withAngleBrackets, lastComma=$lastComma, lastColumn=$lastColumn, hasAtSymbol=$hasAtSymbol, nonLetterOrDigit=$nonLetterOrDigit, nonAlphabetic=$nonAlphabetic)"
        }


    }

    fun check(regexp: String) = Pattern.matches(regexp, text)

    private fun getTokenType(): TokenType {
        Static.types.forEach { pair ->
            if (check(pair.second.regex)) {
                return@getTokenType pair.first
            }
        }
        return TokenType.UNDEFINED
    }

    private fun getAttributes() = Attributes()

    fun getDifference(other: Token): Int {
        var difference = 0;

        if (this.type != other.type)
            difference += REPLACEMENT_COST[this.type.ordinal][other.type.ordinal]
        else {
            val lengthDiff = Math.abs(this.text.length - other.text.length)
            if (this.type == TokenType.DIGITS && lengthDiff > 0)
                difference += DIGITS_INEQUALITY_COST
        }

        difference += getAttributesDifference(other)

        return difference
    }

    private fun getAttributesDifference(other: Token): Int {
        var difference = 0

        difference += attributeDifference(this.attrs.lastComma, other.attrs.lastComma)
        difference += attributeDifference(this.attrs.withAngleBrackets, other.attrs.withAngleBrackets)
        difference += attributeDifference(this.attrs.lastColumn, other.attrs.lastColumn)
        difference += attributeDifference(this.attrs.hasAtSymbol, other.attrs.hasAtSymbol)
        difference += attributeDifference(this.attrs.nonLetterOrDigit, other.attrs.nonLetterOrDigit)
        difference += attributeDifference(this.attrs.nonAlphabetic, other.attrs.nonAlphabetic)

        return difference
    }

    private fun attributeDifference(firstAttribute: Boolean, secondAttribute: Boolean): Int =
            if (firstAttribute != secondAttribute)
                ATTRIBUTE_INEQUALITY_COST
            else
                0

    fun getInsertionCost(): Int = INSERTION_COST[type] ?: throw IllegalArgumentException()


    fun getDeletionCost() = getInsertionCost()

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
        result += 13 * result + this.attrs.hashCode()
        return result
    }

    override fun toString(): String {
        return "Token(text='$text', type=$type, attrs=$attrs)"
    }


}

