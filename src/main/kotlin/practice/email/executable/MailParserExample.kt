package practice.email.executable

import practice.email.parser.parseEml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            println(parseEml(file))
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }


}

// just some func for testing
fun helperFun() {
    val props: Properties = System.getProperties()
    val mailSession: Session = Session.getDefaultInstance(props)
    val source: InputStream = FileInputStream(File("D:\\IDEA workspace\\email-parser\\src\\test\\resources\\yamail-yamail\\wc_test.eml"))
    val message: MimeMessage = MimeMessage(mailSession, source)

    //    val sc: Scanner = Scanner(FileInputStream(File("src\\main\\resources\\1_1_send_2_q0.eml")))
    //    var sb = StringBuffer()
    //    while (sc.hasNextLine()) {
    //        sb.append(sc.nextLine()+ "\n")
    //    }
    //    val text = sb.toString()
    //    println(text)
    //    val source: InputStream = ByteArrayInputStream(text.toByteArray(Charset.defaultCharset()))
    //    val message: MimeMessage = MimeMessage(mailSession, source)

    println("Date : " + message.sentDate)
    for (a in message.from) {
        val addr = a as InternetAddress
        println("From : ${addr.address}")
    }
    for (t in message.getRecipients(Message.RecipientType.TO)) {
        val addr = t as InternetAddress
        println("To : ${addr.address}")
    }
    println("Subject : " + message.subject)
    println("--------------")
    val content = message.content
    if (content is MimeMultipart) {
        println(content.getBodyPart(0).content)
    } else {
        println(content)
    }


    //    val headers =  message.allHeaders
    //    while (headers.hasMoreElements()) {
    //        val h: Header = headers.nextElement() as Header
    //        println("${h.name}: ${h.value}")
    //    }
}
