package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.preprocess
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*


private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
private val pathEmails = "D:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 1000
fun main(args: Array<String>) {

    val withHeaders = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}withHeaders_weak.txt"
    ))))
    val empty = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}empty_weak.txt"
    ))))

    var emptyCount = 0
    var headerCount = 0

    var i = 0
    while (emptyCount + headerCount < EMAILS_COUNT) {
        var filterThisMessage = false
        val header: List<String>?
        var from = -1
        var to = -1
        val email: Email
        try {
            email = EmailParser(
                    File("${pathEmails}${i}.eml")
            ).parse()

            if (!email.content.body.lines()[0].trim().equals(FILTER_STRING)) {
                val (f, t, h) = QuotesHeaderSuggestions.getQuoteHeaderWithIndexes(
                        email.content.body
                )
                from = f
                to = t
                header = h
            } else {
                header = null
                filterThisMessage = true
            }

        } catch(e: Exception) {
            println("${i}.eml gave an error while parsing: ${e.message}")
            println("Skipping...")
            i++
            continue
        } catch(e: Error) {
            println("Some error with eml ${i}.")
            println("Message: ${e.message}")
            println("Skipping...")
            i++
            continue
        }

        val body = email.content.body.lines()
        if (header != null) {
            headerCount++
            withHeaders.write("${i}\n\n\n")
            body.mapIndexed { i, s ->
                if (i >= from && i <= to) {
                    "!!!\t${s.toUpperCase()}"
                } else {
                    s
                }
            }.filterIndexed { i, s ->
                i <= to+10
            }.forEach {
                withHeaders.write(it)
                withHeaders.newLine()
            }
            withHeaders.write("\n\n\n")
            withHeaders.write("X".repeat(150))
            withHeaders.newLine()
            withHeaders.write("X".repeat(150))
            withHeaders.write("\n")
        } else if (!filterThisMessage) {
            emptyCount++
            empty.write("${i}\n\n\n")
            body.forEach {
                empty.write(it)
                empty.newLine()
            }
            empty.write("\n\n\n")
            empty.write("X".repeat(150))
            empty.newLine()
            empty.write("X".repeat(150))
            empty.write("\n")
        }

        if (i % 100 == 0) {
            println("${i} is passed")
        }
        i++
    }

    println("Done.")
    
    println("$headerCount with headers.")
    println("$emptyCount without headers.")
    println("${i - headerCount - emptyCount} skipped.")
}