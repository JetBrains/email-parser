package quoteParser.executable

import quoteParser.*
import java.io.*

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"

private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 23055

fun main(args: Array<String>) {
    val emlDir: File
    if (args.size > 0) {
        emlDir = File(args[0])
        if (emlDir.exists() && emlDir.isDirectory) {
            getData(emlDir)
        } else {
            println("Incorrect path.")
        }
    } else {
        println("Input path to directory with emails as a first command-line argument.")
    }
}

fun getData(emlDir: File) {
    val dataSet = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}expected_headers.txt"
    ))))
    var headersCount = 0
    for (i in 0..EMAILS_COUNT - 1) {

        val header: List<String>?
        val emailText: List<String>
        try {
            val emlFile = File(emlDir, "${i}.eml")
            val msg = getMimeMessage(emlFile)
            emailText = getEmailText(msg).lines()

            if (!emailText[0].trim().equals(FILTER_STRING)) {
                val qp = QuoteParser.Builder()
                        .hasInReplyToEMLHeader(containInReplyToHeader(msg))
                        .deleteQuoteMarks(false)
                        .build()
                val c = qp.parse(emailText)
                val H = c.header
                if (H != null && H.text.isEmpty()) {
                    header = listOf(c.quote!!.body[0])
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
