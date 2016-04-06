package practice.email.executable

import practice.email.parser.Content
import practice.email.parser.EmailContentParser
import practice.email.parser.EmailParser
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            val parser = EmailParser()
            var email1 = parser.parseEml(file)
            println("********** Just an email **********")
            println(email1)

            var email2 = parser.parseEmlWithContent(file, EmailContentParser.MODE_ONE)
//            println("********** Email with parsed content **********")
//            println(email2)
            println("********** Content's components (mode ONE) **********")
            println("*** body ***")
            println(email2.content.body)
            println("*** quote ***")
            println(email2.content.quote)
            println("*** signature ***")
            println(email2.content.signature)
            println()
            
            var email3 = parser.parseEmlWithContent(file, EmailContentParser.MODE_DEEP)
            println("********** Content's components (mode DEEP) **********")
            
            var currentContent: Content? = email3.content
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
