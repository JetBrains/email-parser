package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.preprocess
import java.io.*
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
private val pathEmails = "D:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 23055

fun main(args: Array<String>) {
    val dataSet = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}data.txt"
    ))))
    var headersCount = 0
    for (i in 0.. EMAILS_COUNT - 1) {

        val header: List<String>?
        val email: Email
        try {
            email = EmailParser(
                    File("${pathEmails}${i}.eml")
            ).parse()

            if (!email.content.body.lines()[0].trim().equals(FILTER_STRING)) {
                header = QuotesHeaderSuggestions.getQuoteHeader(
                        email.content.body
                )
            } else {
                header = null
            }

        } catch(e: Exception) {
            println("${i}.eml gave an error while parsing: ${e.message}")
            println("Skipping...")
            continue
        } catch(e: Error) {
            println("Some error with eml ${i}.")
            println("Message: ${e.message}")
            println("Skipping...")
            continue
        }

        if (header != null) {

            try {
                val headerLine = preprocess(header)
                dataSet.write("${i}\t\t")
                dataSet.write(headerLine)
                dataSet.newLine()
                headersCount++
            } catch (e: StringIndexOutOfBoundsException) {
                println("Indexing error with eml ${i}.")
                throw e
            }
        }

        if (i % 100 == 0) {
            println("${i} is passed")
        }
    }

    dataSet.close()

    println("Done.")
    println("${headersCount} headers were found.")
}
