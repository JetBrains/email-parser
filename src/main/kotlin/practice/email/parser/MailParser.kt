package practice.email.parser

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.*
import javax.mail.internet.*

/**
 * Contains email body content parse mode constants.
 */
object EmailContentParseMode {
    /**
     * Return plain email body content without any parsing.
     */
    val SIMPLE = 0
    val ONE = 1
    val DEEP = 2
}

private val defaultCharset = "UTF-8"

/**
 * Contains email's Content-Type header values this library is currently able to parse.
 */
private object ContentType {
    val TEXT_PLAIN = "text/plain"
    val MULTIPART_ALT = "multipart/alternative"

}

// Templates for detecting quotes and signatures.
// Some others will be added later.
val SIGNATURE_PATTERN = "--"

val QUOTE_PATTERN = ">"

/**
 * Data class for storing some parsed email headers and parsed email body content.
 */
data class Email(val date: Date,
                 val from: ArrayList<String>,
                 val to: ArrayList<String>,
                 val subject: String,
                 var content: Content) {
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
 * @return object of Email class.
 * @exception ParseException if fails to parse Date, From or Content fields.
 * @exception NotImplementedError
 */
fun parseEml(emlFile: File): Email {
    val source: InputStream = FileInputStream(emlFile)
    return parse(source)
}

// TODO add comments
// TODO parseEmailContent eats(adds?) one of CRLF(example - yamail-yamail 4_1_receive_q1). Fix it
// TODO It seems there are mey be some problems with quotes and signs in one email simultaneously
/**
 * 
 *
 */
fun parseEmlWithContent(emlFile: File, mode: Int = EmailContentParseMode.SIMPLE): Email {
    val email = parseEml(emlFile)
    //check for incorrent mode
    if (mode != EmailContentParseMode.SIMPLE) {
        email.content = parseEmailContent(email.content.body, mode)
    }
    return email
}

/**
 *
 */
private fun parseEmailContent(content: String, mode: Int): Content {
    val lines: Array<String> = content.split("\n").toTypedArray();
    var linesIdx = lines.size - 1
    var buffer: Array<String> = Array(lines.size) {""}
    var bufferLength = 0
    var body: String
    var quote: Content? = null
    var sign:  String? = null
    
    while (linesIdx > -1 && 
           !lines[linesIdx].trim().equals(SIGNATURE_PATTERN) && 
           !lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
        buffer[bufferLength++] = lines[linesIdx--]
    }
    
    if (linesIdx <= -1) {
        body = reverseAndJoin(buffer, bufferLength)
        body += "\n"
        return Content(body, quote, sign)
    }
    
    if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
        bufferLength = 0
        buffer[bufferLength++] = lines[linesIdx--].substring(1)
        while (linesIdx > -1 && !lines[linesIdx].equals("")) {
            if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
                buffer[bufferLength++] = lines[linesIdx--].substring(1)
            } else {
                buffer[bufferLength++] = lines[linesIdx--]
            }
        }
        val quoteString = reverseAndJoin(buffer, bufferLength)
        if (mode == EmailContentParseMode.DEEP) {
            quote = parseEmailContent(quoteString, mode)
        } else {
            quote = Content(quoteString, null, null)
        }
        body = lines.take(linesIdx + 1).joinToString(separator = "\n")
        body += "\n"
        return Content(body, quote, sign)
    }
    
    if (lines[linesIdx].trim().equals(SIGNATURE_PATTERN)) {
        buffer[bufferLength++] = lines[linesIdx--]
        sign = reverseAndJoin(buffer, bufferLength)
        bufferLength = 0
        while ((linesIdx > -1) && !lines[linesIdx].trim().startsWith(QUOTE_PATTERN))  {
            buffer[bufferLength++] = lines[linesIdx--]
        }
        if (linesIdx <= -1) {
            body = reverseAndJoin(buffer, bufferLength)
            body += "\n"
            return Content(body, quote, sign)
        }
        
        // TODO find a way get rid of duplicate code.
        // Repeated code. Is there in Kotlin convenient way to move it into
        // the separate function?
        if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
            bufferLength = 0
            buffer[bufferLength++] = lines[linesIdx--].substring(1)
            while (linesIdx > -1 && !lines[linesIdx].equals("")) {
                if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
                    buffer[bufferLength++] = lines[linesIdx--].substring(1)
                } else {
                    buffer[bufferLength++] = lines[linesIdx--]
                }
            }
            val quoteString = reverseAndJoin(buffer, bufferLength)
            if (mode == EmailContentParseMode.DEEP) {
                quote = parseEmailContent(quoteString, mode)
            } else {
                quote = Content(quoteString, null, null)
            }
            body = lines.take(linesIdx + 1).joinToString(separator = "\n")
            body += "\n"
            return Content(body, quote, sign)
        }
    }

    // Must never get there.
    throw ParseException("Incorrect message content parsing.")
}

fun reverseAndJoin(buffer: Array<String>, bufferLength: Int): String =
    if (bufferLength == 0) {
        ""
    } else {
        val res = buffer.take(bufferLength).toTypedArray()
        res.reverse()
        res.joinToString(separator = "\n")
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

private fun getErrorMsg(fieldName: String) = "Could not found '$fieldName' field."

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


// TODO add more tests for this fun
// TODO It seems that charset is fetched incorrectly and cause incorrect body decoding
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
    for ((i, elem) in part.contentType.split(";").withIndex()) {
        when (i) {
            0 -> contentType = elem.trim()
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
            return Content(
                    decodeTo(content as String, charset, defaultCharset).replace("\r\n", "\n").replace("\r", "\n"),
                    null, null
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
