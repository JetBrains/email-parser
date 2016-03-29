package practice.email.parser

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.*
import javax.mail.internet.*

// TODO Add tests

/**
 * Contains email body content parse mode constants.
 */
object EmailContentParseMode {
    /**
     * Return plain email body content without any parsing.
     */
    val SIMPLE = 0

    //reserved
    val QUOTE = 1
    val QUOTE_DEEP = 2
    val QUOTE_SMART = 3
    val QUOTE_SMART_DEEP = 4
}

private val defaultCharset = "UTF-8"

/**
 * Contains email's Content-Type header values this library is currently able to parse.
 */
private object ContentType {
    val TEXT_PLAIN = "text/plain"
    val MULTIPART_ALT = "multipart/alternative"
}

/**
 * Chosen parse mode for email body content.
 */
private var contentParseMode = EmailContentParseMode.SIMPLE

/**
 * Defines the depth of the quotes parsing in the email body.
 * Default value is 0.
 *
 * Not used for now. Reserved.
 */
private var contentParseDeep = 0

/**
 * Data class for storing some parsed email headers and parsed email body content.
 */
data class Email(val date: Date,
                 val from: ArrayList<String>,
                 val to: ArrayList<String>,
                 val subject: String,
                 val content: Content) {
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

/**
 * Data class for storing email body, quote and signature.
 */
data class Content(val body: String, val quote: Content?, val signature: String?) {
    override fun toString(): String {
        return StringBuilder(body)
                .append(quote ?: "")
                .append(signature ?: "")
                .toString()
    }
}

/**
 * Return parsed email with Date, From, To, Subject and Content fields.
 *
 * @param emlFile file in EML format.
 * @param contentParsingMode email body content parser mode. Default value is EmailContentParseMode.SIMPLE.
 * @param contentParsingDeep defines the depth of the quotes parsing in the email body. Default value is 0.
 * @return object of Email class.
 * @exception ParseException if fails to parse Date, From or Content fields.
 * @exception NotImplementedError
 */
fun parseEml(emlFile: File, contentParsingMode: Int = EmailContentParseMode.SIMPLE, contentParsingDeep: Int = 0): Email {
    val source: InputStream = FileInputStream(emlFile)
    contentParseMode = contentParsingMode
    contentParseDeep = contentParsingDeep
    return parse(source)
}

/**
 * Return parsed email with Date, From, To, Subject and Content fields.
 *
 * @param emlString string with all EML text inside it.
 * @param contentParsingMode email body content parser mode. Default value is EmailContentParseMode.SIMPLE.
 * @param contentParsingDeep defines the depth of the quotes parsing in the email body. Default value is 0.
 * @return object of Email class.
 * @exception ParseException if fails to parse Date, From or Content fields.
 * @exception NotImplementedError
 */
fun parseEml(emlString: String, mode: Int = EmailContentParseMode.SIMPLE, deep: Int = 1): Email {
    val source: InputStream = ByteArrayInputStream(emlString.toByteArray(charset("US-ASCII")))
    contentParseMode = mode
    contentParseDeep = deep
    return parse(source)
}

/**
 * Create MimeMessage from the given source and fetch and decode all needed fields.
 *
 * @param source  InputStream with email source in EML format.
 * @return object of Email class.
 * @exception ParseException if fails to parse Date, From or Content fields.
 * @exception NotImplementedError
 */
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
    // Not necessary field (from RFC 822)
    //    if (from.size == 0) {
    //        throw ParseException(getErrorMsg("To"))
    //    }

    val subject: String = msg.subject ?: "" // throw ParseException(getErrorMsg("Subject")) // Not necessary field (from RFC 822)

    val content: Content = parseContent(msg)

    return Email(date, from, to, subject, content)
}

private fun getErrorMsg(fieldName: String) = "Coud not found '$fieldName' field."

/**
 * Transform addresses from array of Address objects to their string representation.
 *
 * @param addrresses array of Address objects.
 * @return ArrayList of addresses' string representation.
 */
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

/**
 * This function defines email message type, amount of parts it is consists of,
 * gets the appropriate part and parses it.
 *
 * @param part parsed part of email message (email messages with multipart Content-type consists of several MimePart's).
 * @return object of Content class.
 * @exception ParseException if fails to parse Content field.
 * @exception NotImplementedError for now it works only with text/plain and multipart/alternative Content-types.
 */
private fun parseContent(part: MimePart): Content {
    val content: Any = part.content ?: throw ParseException(getErrorMsg("Content"))
    var contentType: String = "unknown"
    var charset: String = "UTF-8"

    // Parse contentType and charset if given
    for ((i, elem) in part.contentType.split("; ").withIndex()) {
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
        ContentType.TEXT_PLAIN ->
            return splitContent(
                    decodeTo(content as String, charset, defaultCharset)
            )
        ContentType.MULTIPART_ALT ->
            return parseContent(
                    (content as MimeMultipart).getBodyPart(0) as MimePart
            )
        else -> throw NotImplementedError()
    }
}

/**
 * Decode the given content from one charset to another.
 *
 * @param content string to decode.
 * @param charsetFrom string representation of source charset.
 * @param charsetTo string representation of target charset.
 * @return decoded string.
 */
private fun decodeTo(content: String, charsetFrom: String, charsetTo: String): String =
        content.toByteArray(charset(charsetFrom)).toString(charset(charsetTo))

/**
 * Forming Content object from the given content string depends from
 * contentParseMode and contentParseDeep.
 *
 * @param content email message content in form of plain text.
 * @return Content object.
 * @exception NotImplementedError for now it works just with EmailContentParseMode.SIMPLE mode.
 */
private fun splitContent(content: String): Content {
    when (contentParseMode) {
        EmailContentParseMode.SIMPLE -> return Content(content, null, null)
        else -> throw NotImplementedError()
    }
}
