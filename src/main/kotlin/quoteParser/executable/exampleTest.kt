package quoteParser.executable

import quoteParser.*
import java.io.*
import javax.mail.internet.MimeMessage

private val pathEmails = "C:${File.separator}YT${File.separator}"

// todo modify
fun main(args: Array<String>) {
    val emlFile = File("""C:\Users\pavel.zhuk\IdeaProjects\email-parser\src\main\resources\fwd\Fwd_gmail_tb.eml""")
    val start = System.currentTimeMillis()
    val msg: MimeMessage = getMimeMessage(emlFile)
    val emailText: String = getEmailText(msg)
    val isInReplyToHeader = containInReplyToHeader(msg)
    val trigger = false
    val content = QuoteParser.Builder()
            .hasInReplyToEMLHeader(isInReplyToHeader)
            .deleteQuoteMarks(true)
            .recursive(true)
            .build().parse(emailText.lines())
    val end = System.currentTimeMillis()
    println("Working time: ${end - start} ms.")

    println(content.toString(addMarks = true, uppercaseHeader = true))
}