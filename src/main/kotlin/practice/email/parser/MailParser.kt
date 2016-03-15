package practice.email.parser

import java.util.*

object ParseMode{
    val SIMPLE = 0
    val QUOTE  = 1
    val QUOTE_DEEP = 2
    val QUOTE_SMART = 3
    val QUOTE_SMART_DEEP = 4
}

data class Email(val from: String, val to: String, val date: Date, val subject: String, val content: EmailContent)
data class EmailContent(val doby: String)

fun parseEml(path: String, mode: Int = ParseMode.SIMPLE, deep: Int = 1): Email? {
    // not implemented yet
    return null
}
