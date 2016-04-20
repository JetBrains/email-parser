package practice.email.executable

import practice.email.parser.Content
import practice.email.parser.ContentParseMode
import practice.email.parser.Email
import practice.email.parser.EmailContentParser
import practice.email.parser.EmailParser
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            var mode =  ContentParseMode.MODE_DEEP
            if (args.size == 2 && args[1] == "simple") {
                mode = ContentParseMode.MODE_SIMPLE
            }
            val parser = EmailParser(file, mode)
            var email = parser.parse()
            when (mode) {
                ContentParseMode.MODE_SIMPLE -> println(email)
                ContentParseMode.MODE_DEEP   -> println(email.getTreeRepresentation())
            }
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }
}