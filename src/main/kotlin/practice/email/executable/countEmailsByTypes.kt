package practice.email.executable

import quoteParser.getEmailText
import java.io.*
import javax.mail.internet.ParseException

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"

private val EMAILS_COUNT = 23055 /*+ cnt*/

fun main(args: Array<String>) {
    val emlDir: File
    if (args.size > 0) {
        emlDir = File(args[0])
        if (emlDir.exists() && emlDir.isDirectory) {
            countEmailsByTypes(emlDir)
        } else {
            println("Incorrect path.")
        }
    } else {
        println("Input path to directory with emails as a first command-line argument.")
    }
}

fun countEmailsByTypes(emlDir: File) {
    val textPlain = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}textPlain.txt"
    ))))
    val nonTextPlain = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}nonTextPlain.txt"
    ))))
    var textPlainCount = 0
    var nonTextPlainCount = 0
    for (i in 23055.. EMAILS_COUNT - 1) {
        try {
            val file = File(emlDir, "${i}.eml")
            val emailText = getEmailText(file)

            textPlain.write(i.toString())
            textPlain.newLine()
            textPlainCount++

        } catch(e: ParseException) {
            println("${i}.eml: ${e.message}")

            nonTextPlain.write(i.toString())
            nonTextPlain.newLine()
            nonTextPlainCount++
        } catch(e: Exception) {
            println("${i}.eml gave an error while parsing: ${e.message}")
            println("Skipping...")
        } catch(e: Error) {
            println("Some error with eml ${i}.")
            println("Message: ${e.message}")
            println("Skipping...")
        }

        if (i % 100 == 0) {
            println("${i} is passed")
        }
    }

    textPlain.close()
    nonTextPlain.close()

    println("Done.")
    println("${textPlainCount} emails with text/plain.")
    println("${nonTextPlainCount} emails without text/plain.")
}


