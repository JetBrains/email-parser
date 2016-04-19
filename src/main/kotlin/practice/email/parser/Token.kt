package practice.email.parser

import java.util.regex.Pattern

val COMMA_END_REGEX = ",?"

val DIGITS_REGEX = "\\d+" + COMMA_END_REGEX
val ANGLE_BRACETS_REGEX = "<.*>" + COMMA_END_REGEX
val COMMA_REGEX = ".*,"
val EMAIL_REGEX = "<?.+@.+\\..+>?"
val TIME_REGEX  = "([01]?[0-9]|2[0-3]):[0-5][0-9]" + COMMA_END_REGEX
val MERIDIEM_REGEX = "(A|a|P|p)\\.?(M|m)\\.?" + COMMA_END_REGEX
val DATE_REGEX = "[0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2}" + COMMA_END_REGEX
val DATE_REVERSE_REGEX = "(?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]" + COMMA_END_REGEX
val LAST_TOKEN_REGEX = ".*\\n"
val LAST_TOKEN_COLUMN_REGEX = ".*:\\n"

class Token(val text: String) {
    val isDigits   = check(DIGITS_REGEX)
    val isEmail = check(EMAIL_REGEX)
    val isTime = check(TIME_REGEX)
    val isMeridiem = check(MERIDIEM_REGEX)
    val isDate = check(DATE_REGEX) || check(DATE_REVERSE_REGEX)
    val isLastToken = check(LAST_TOKEN_REGEX)

    val hasLastComma = check(COMMA_REGEX)
    val hasWithAngleBrackets = check(ANGLE_BRACETS_REGEX)
    val hasLastTokenColumn = check(LAST_TOKEN_COLUMN_REGEX)
    
    
    private fun check(regexp: String) = Pattern.matches(regexp, text)
}