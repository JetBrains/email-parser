package practice.email.parser

import java.util.regex.Pattern

val COMMA_END_REGEX = ",?"

val DIGITS_REGEX = "\\d+" + COMMA_END_REGEX
val ANGLE_BRACETS_REGEX = "<.*>" + COMMA_END_REGEX
val COMMA_REGEX = ".*,"
val EMAIL_REGEX = "<?.+@.+\\..+>?"
val TIME_REGEX = "([01]?[0-9]|2[0-3]):[0-5][0-9]" + COMMA_END_REGEX
val MERIDIEM_REGEX = "(A|a|P|p)\\.?(M|m)\\.?" + COMMA_END_REGEX
val DATE_REGEX = "[0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2}" + COMMA_END_REGEX
val DATE_REVERSE_REGEX = "(?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]" + COMMA_END_REGEX
val LAST_TOKEN_REGEX = ".*\\n"
val LAST_TOKEN_COLUMN_REGEX = ".*:\\n"

class Token(val text: String) {

    val isDigits = check(DIGITS_REGEX)
    val isEmail = check(EMAIL_REGEX)
    val isTime = check(TIME_REGEX)
    val isMeridiem = check(MERIDIEM_REGEX)
    val isDate = check(DATE_REGEX) || check(DATE_REVERSE_REGEX)
    val isLastToken = check(LAST_TOKEN_REGEX)

    val hasLastComma = check(COMMA_REGEX)
    val hasWithAngleBrackets = check(ANGLE_BRACETS_REGEX)
    val hasLastTokenColumn = check(LAST_TOKEN_COLUMN_REGEX)


    private fun check(regexp: String) = Pattern.matches(regexp, text)

    fun getDifference(other: Token): Int {
        var res = 0;

        val lengthDiff = Math.abs(this.text.length - other.text.length)
        
        if (isDigits != other.isDigits) {
            res++
        } 
        else if (isDigits && other.isDigits && lengthDiff > 1) {
            res++
        }
        if (isEmail != other.isEmail) 
            res++
        if (isTime != other.isTime) 
            res++
        if (isMeridiem != other.isMeridiem) 
            res++
        if (isDate != other.isDate) 
            res++
        if (isLastToken != other.isLastToken) 
            res++
        
        if (hasLastComma != other.hasLastComma) 
            res++
        if (hasWithAngleBrackets != other.hasWithAngleBrackets) 
            res++
        if (hasLastTokenColumn != other.hasLastTokenColumn) 
            res++

        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Token

        if (isDigits != other.isDigits) return false
        if (isEmail != other.isEmail) return false
        if (isTime != other.isTime) return false
        if (isMeridiem != other.isMeridiem) return false
        if (isDate != other.isDate) return false
        if (isLastToken != other.isLastToken) return false
        if (hasLastComma != other.hasLastComma) return false
        if (hasWithAngleBrackets != other.hasWithAngleBrackets) return false
        if (hasLastTokenColumn != other.hasLastTokenColumn) return false

        return true
    }
    
    override fun hashCode(): Int{
        var result = text.hashCode()
        result += 31 * result + isDigits.hashCode()
        result += 31 * result + isEmail.hashCode()
        result += 31 * result + isTime.hashCode()
        result += 31 * result + isMeridiem.hashCode()
        result += 31 * result + isDate.hashCode()
        result += 31 * result + isLastToken.hashCode()
        result += 31 * result + hasLastComma.hashCode()
        result += 31 * result + hasWithAngleBrackets.hashCode()
        result += 31 * result + hasLastTokenColumn.hashCode()
        return result
    }

}