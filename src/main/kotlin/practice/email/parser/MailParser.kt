package practice.email.parser

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.*
import javax.mail.internet.*

// TODO Code refactoring
// TODO Add tests
// TODO fix gradle run arg
// TODO fix incorrrect charset in the console

// Points in witch mode parse mail body
object ParseMode {
    val SIMPLE = 0

    //reserved
    val QUOTE = 1
    val QUOTE_DEEP = 2
    val QUOTE_SMART = 3
    val QUOTE_SMART_DEEP = 4
}

// processed content types
object ContentType {
    val TEXT_PLAIN = "text/plain"
    val MULTIPART_ALT = "multipart/alternative"
}

private var MODE = ParseMode.SIMPLE
private var DEEP = 0

data class Email(val date: Date, val from: ArrayList<String>, val to: ArrayList<String>, val subject: String, val content: Content) {
    override fun toString(): String {
        val builder = StringBuilder()

        builder.append("Date: ${date}\n")
        for (f in from) {
            builder.append("From: ${f}\n")
        }
        for (t in to) {
            builder.append("To: ${t}\n")
        }
        builder.append("Subject: ${subject}\n")
        builder.append("-----------------------------------\n")
        builder.append(content)
        return builder.toString()
    }
}

data class Content(val body: String, val quote: Content?, val signature: String?) {
    override fun toString(): String {
        return StringBuilder(body)
                .append(quote ?: "")
                .append(signature ?: "")
                .toString()
    }
}

fun parseEml(emlFile: File, mode: Int = ParseMode.SIMPLE, deep: Int = 1): Email {
    val source: InputStream = FileInputStream(emlFile)
    MODE = mode
    DEEP = deep
    return parse(source)
}

fun parseEml(emlString: String, mode: Int = ParseMode.SIMPLE, deep: Int = 1): Email {
    val source: InputStream = ByteArrayInputStream(emlString.toByteArray(charset("US-ASCII")))
    MODE = mode
    DEEP = deep
    return parse(source)
}

private fun parse(source: InputStream): Email {
    val props: Properties = System.getProperties()
    val session: Session = Session.getDefaultInstance(props)
    val msg: MimeMessage = MimeMessage(session, source)

    val date: Date = msg.sentDate ?: throw ParseException(getErrorMsg("Date"))

    val from: ArrayList<String> = getAddresses(msg.from)
    if (from.size == 0) {
        throw ParseException(getErrorMsg("From"))
    }

    val to: ArrayList<String> = getAddresses(msg.getRecipients(Message.RecipientType.TO))
    if (from.size == 0) {
        throw ParseException(getErrorMsg("To"))
    }

    val subject: String = msg.subject ?: throw ParseException(getErrorMsg("Subject"))

    val content: Content = parseContent(msg)

    return Email(date, from, to, subject, content)
}

private fun getErrorMsg(fieldName: String) = "Coud not found '$fieldName' field."

private fun getAddresses(addrresses: Array<Address>): ArrayList<String> {
    val addressList = ArrayList<String>()
    for (a in addrresses) {
        val addr = a as InternetAddress
        if (addr.personal == null) {
            addressList.add(addr.address)
        } else {
            addressList.add("${addr.personal} <${addr.address}>")
        }
    }
    return addressList
}

private fun parseContent(part: MimePart): Content {
    val content: Any = part.content ?: throw ParseException(getErrorMsg("Content"))
    var contentType: String = "unknown"
    var charset: String = "UTF-8"
    for ((i, elem)in part.contentType.split("; ").withIndex()) {
        when (i) {
            0 -> contentType = elem
            else -> {
                val s = elem.split("=")
                if (s[0].equals("charset")) {
                    charset = s[1]
                }
            }
        }
    }
    when (contentType) {
        ContentType.TEXT_PLAIN -> return splitContent((content as String)
                                                .toByteArray(charset(charset))
                                                .toString(charset("UTF-8")))
        ContentType.MULTIPART_ALT -> return parseContent((content as MimeMultipart)
                                                            .getBodyPart(0) as MimePart)
        else -> throw NotImplementedError()
    }
}

private fun splitContent(content: String): Content {
    when (MODE) {
        ParseMode.SIMPLE -> return Content(content, null, null)
        else -> throw NotImplementedError()
    }
}
