package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import quoteParser.Content
import quoteParser.getEmailText
import quoteParser.parse
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"

private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 1000
fun main(args: Array<String>) {
    val emlDir: File
    if (args.size > 0) {
        emlDir = File(args[0])
        if (emlDir.exists() && emlDir.isDirectory) {
            skipTest(emlDir)
        } else {
            println("Incorrect path.")
        }
    } else {
        println("Input path to directory with emails as a first command-line argument.")
    }
}

fun skipTest(emlDir: File) {
    val withHeaders = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}withHeaders.txt"
    ))))
    val empty = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}empty.txt"
    ))))

    var emptyCount = 0
    var headerCount = 0

    var i = 0
    while (i < 23055 && emptyCount + headerCount < EMAILS_COUNT) {

        try {

            val file = File(emlDir, "${i}.eml")
            val emailText = getEmailText(file).lines()

            if (emailText[0].trim().equals(FILTER_STRING)) {
                i++
                continue
            }

            val c = parse(file)

            if (c.header == null) {
                write(empty, c, i)
                emptyCount++
            } else {
                write(withHeaders, c, i)
                headerCount++
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

        if (i % 100 == 0) {
            println("${i} is passed")
        }

        i++
    }

    println("Done.")

    println("$headerCount with headers.")
    println("$emptyCount without headers.")
    println("${i - headerCount - emptyCount} skipped.")

    withHeaders.close()
    empty.close()
}

fun write(out: BufferedWriter, c: Content, i: Int) {
    out.write(i.toString())
    out.newLine()
    c.body.forEach { out.write(it); out.newLine() }
    c.header?.text?.forEach {out.write("> "); out.write(it.toUpperCase()); out.newLine() }
    if (c.quote != null) {

        val xx = c.quote.body.size
        val yy = if (10 > xx) xx else 10
        c.quote.body.slice(0..yy-1).forEach {out.write("> ");  out.write(it); out.newLine() }
    }
    out.newLine()
    out.newLine()
    out.write("X".repeat(150))
    out.newLine()
    out.write("X".repeat(150))
    out.newLine()
}
