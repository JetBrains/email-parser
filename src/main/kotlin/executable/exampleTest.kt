package executable

import quoteParser.*
import java.io.*
import javax.mail.internet.MimeMessage

private val pathEmails = "C:${File.separator}YT${File.separator}"

fun main(args: Array<String>) {    
    val emlFile = File("""D:\YT\6510.eml""")
    val start = System.currentTimeMillis()
    val trigger = true
    val content = QuoteParser.Builder()
            .deleteQuoteMarks(false)
            .recursive(false)
            .build().parse(emlFile)
    val end = System.currentTimeMillis()
    println("Working time: ${end - start} ms.")

    println(content.toString(addMarks = false, uppercaseHeader = true))
}