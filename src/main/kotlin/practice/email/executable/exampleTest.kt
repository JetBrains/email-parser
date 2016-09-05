package practice.email.executable

import quoteParser.*
import java.io.*
import javax.mail.internet.MimeMessage

private val pathEmails = "C:${File.separator}YT${File.separator}"

fun main(args: Array<String>) {
    val emlFile = File("${pathEmails}9757.eml")
    val start = System.currentTimeMillis()
    val msg: MimeMessage = getMimeMessage(emlFile)
    val emailText: String = getEmailText(msg)
    val isInReplyToHeader = containInReplyToHeader(msg)
    val trigger = false
    val content = QuoteParser(
            isInReplyToEMLHeader = isInReplyToHeader,
            deleteQuoteMarks = false,
            recursive = trigger
    ).parse(emailText.lines())
    val end = System.currentTimeMillis()
    println("Working time: ${end - start} ms.")

    println(content.toString(addMarks = false, uppercaseHeader = true))
}