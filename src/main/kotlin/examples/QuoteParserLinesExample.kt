package examples

import quoteParser.QuoteParser
import quoteParser.containInReplyToHeader
import quoteParser.getEmailText
import quoteParser.getMimeMessage
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

// This is an example of usage QuoteParser with list of strings.
// You can specify if the supposed eml file contain In-Reply-To header
// through the call of the Builder().hasInReplyToEMLHeader(value).
// It has true default value which means that quotation criteria are weakened.
//
// Also you could use helper functions to get MimeMessage, check for 
// In-Reply-To header or extract plain text from MimeMessage.
fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            val start = System.currentTimeMillis()

            val msg = getMimeMessage(file)
            val hasInReplyTo = containInReplyToHeader(msg)
            val emlText = getEmailText(msg)
            
            val c = QuoteParser.Builder()
                    .build()
                    .parse(emlText.lines(), hasInReplyTo)

            val end = System.currentTimeMillis()
            println("Working time: ${end - start} ms.")
            println("-".repeat(50))
            println(c.toString(addMarks = true, uppercaseHeader = true))
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }
}