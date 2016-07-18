package practice.email.executable

import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
val pathEmails = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}YT${File.separator}"
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
        val header: String?
        try {
            header = QuotesHeaderSuggestions.getQuoteHeaderLine(
                    EmailParser(
                            File("${pathEmails}${i}.eml")
                    ).parse().content.body
            )     
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
            if (count < DATASETS_SIZE) {
                trainingSet.write("($i)")
                trainingSet.write(header)
                trainingSet.newLine()
            } else {
                testSet.write("($i)")
                testSet.write(header)
                testSet.newLine()   
            }
            count++
        }
        
        if (count % (DATASETS_SIZE * 2 / 5) == 0) {
            println("${count} is passed")
        }
    }

    trainingSet.close()
    testSet.close()
    
    println("Done.")
}