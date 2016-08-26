package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.preprocess
import quoteParser.QuoteParser
import quoteParser.getEmailText
import java.io.*
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
private val pathEmails = "C:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 23055

fun main(args: Array<String>) {
    val dataSet = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}expected_headers.txt"
    ))))
    var headersCount = 0
    for (i in 0.. EMAILS_COUNT - 1) {

        val header: List<String>?
        val email: List<String>
        try {
            email = getEmailText(File("${pathEmails}${i}.eml")).lines()

            if (!email[0].trim().equals(FILTER_STRING)) {
                val H = QuoteParser().parse(email).header
                if (H != null && H.text.isEmpty()) {
                    header = listOf(email[H.startIndex])
                } else {
                    header = H?.text
                }
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
                dataSet.write(i.toString())
                dataSet.newLine()
                header.forEach {
                    dataSet.write(it)
                    dataSet.newLine()
                }
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
