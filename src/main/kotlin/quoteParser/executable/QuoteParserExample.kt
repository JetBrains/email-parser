package quoteParser.executable

import quoteParser.QuoteParser
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            val start = System.currentTimeMillis()
            val c = QuoteParser.Builder().build().parse(file)
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