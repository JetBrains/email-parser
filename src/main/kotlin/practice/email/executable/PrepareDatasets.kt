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

val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
val pathEmails = "C:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

val EMAILS_COUNT = 23055
val DATASETS_SIZE = 100

fun main(args: Array<String>) {
    val used = mutableSetOf(-1)
    var count = 0
    val trainingSet = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}training.txt"
    ))))
    val testSet = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}test.txt"
    ))))
    val random = Random(System.currentTimeMillis())
        
    while (count < DATASETS_SIZE * 2) {
        var i = random.nextInt(EMAILS_COUNT)

        while (i in used) {
            i = random.nextInt(EMAILS_COUNT)
        }
        used.add(i)

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
                if (count < DATASETS_SIZE) {
                    trainingSet.write("${count % 100} - ${i}\t\t")
                    trainingSet.write(headerLine)
                    trainingSet.newLine()
                } else {
                    testSet.write("${count % 100} - ${i}\t\t")
                    testSet.write(headerLine)
                    testSet.newLine()
                }
                count++

            } catch (e: StringIndexOutOfBoundsException) {
                println("Indexing error with eml ${i}.")
                throw e
            }
        }
        
        if (count % (DATASETS_SIZE * 2 / 10) == 0) {
            println("${count} is passed")
        }
    }

    trainingSet.close()
    testSet.close()
    
    println("Done.")
}