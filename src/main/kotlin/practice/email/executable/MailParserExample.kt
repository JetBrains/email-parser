package practice.email.executable

import practice.email.parser.Content
import practice.email.parser.EmailContentParseMode
import practice.email.parser.parseEml
import practice.email.parser.parseEmlWithContent
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            var email = parseEml(file)
            println("********** Just an email **********")
            println(email)
            
            email = parseEmlWithContent(file, EmailContentParseMode.ONE)
            println("********** Content's components (mode ONE) **********")
            println("*** body ***")
            println(email.content.body)
            println("*** quote ***")
            println(email.content.quote)
            println("*** signature ***")
            println(email.content.signature)
            println()
            
            email = parseEmlWithContent(file, EmailContentParseMode.DEEP)
            println("********** Content's components (mode DEEP) **********")
            
            var currentContent: Content? = email.content
            var i = 0
            do {
                println("*** body$i ***")
                println(currentContent?.body)
                println("*** quote$i ***")
                println(currentContent?.quote)
                println("*** signature$i ***")
                println(currentContent?.signature)
                println()
                
                currentContent = currentContent?.quote
                ++i
            } while (currentContent != null)
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }
}
