package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.preprocess
import quoteParser.Content
import quoteParser.QuoteParser
import quoteParser.features.MiddleColonFeature
import quoteParser.getEmailText
import quoteParser.parse
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
    val start = System.currentTimeMillis()
    getActualContents(verbose = true)
    val end = System.currentTimeMillis()
    println("Working time: ${(end - start) / 1000.0} sec.")
}

private fun getActualContents(verbose: Boolean = false){
    if (verbose) {
        println("Evaluateing actual headers...")
    }

    val mcFeature = MiddleColonFeature()
    val out = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}bad_headers.txt"
    ))))
    var cnt = 0
    for (i in 0..EMAILS_COUNT - 1) {
        val content: Content?
        val emailText: List<String>
        try {
            emailText = getEmailText(File("${pathEmails}${i}.eml")).lines()

            if (!emailText[0].trim().equals(FILTER_STRING)) {
                content = QuoteParser(emailText.map { it.trim() }).parse()
            } else {
                content = null
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

        if (content != null && content.header != null) {
            var beforeHeader: Boolean = false
            var afterHeader: Boolean = false
            if (content.body.size != 0 &&
                    mcFeature.matches(content.body[content.body.lastIndex])) {
                beforeHeader = true
            }
            if (content.quote != null && content.quote.body.size != 0 &&
                    mcFeature.matches(content.quote.body[0])) {
                afterHeader = true
            }
            if (!(beforeHeader || afterHeader)) {
                continue
            }
//            var MLheader = true
//            content.header.text?.forEach {
//               if (!mcFeature.matches(it)) {
//                   MLheader = false
//               }
//            }
//            if (!MLheader) {
//                continue
//            }

            cnt++
            try {
                out.write(i.toString())
                out.newLine()
                out.newLine()
                content.body.forEach {
                    out.write(it)
                    out.newLine()
                }
                out.write("------------------------------\n")
                content.header.text?.forEach {
                    out.write(it)
                    out.newLine()
                }
                out.write("------------------------------\n")
                content.quote?.body?.forEachIndexed { i, s ->
                    if (i < 10) {
                        out.write(s)
                        out.newLine()
                    }
                }
                out.newLine()
                out.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n")
            } catch (e: StringIndexOutOfBoundsException) {
                println("Indexing error with eml ${i}.")
                throw e
            }
        }

        if (verbose && i % 100 == 0) {
            println("$i is passed")
        }
    }
    println("Count: $cnt")
    if (verbose) {
        println("Done")
    }
}
