package quoteParser

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimePart
import javax.mail.internet.ParseException

/**
 * Contains email's Content-Type header values this library is currently able to parse.
 */
private object ContentType {
    val TEXT_PLAIN = "text/plain"
    val MULTIPART_ALT = "multipart/alternative"
}

//TODO recursive quote parsing
fun parse(emlFile: File): Content {
    val emailText: String = getEmailText(emlFile)

    // TODO check for special email headers

    return QuoteParser().parse(emailText.lines())
}

fun parseTrimmed(emlFile: File): Content {
    val emailText: String = getEmailText(emlFile)
    return QuoteParser().parse(emailText.lines().map { it.trim() })
}

fun getEmailText(emlFile: File): String {
    val source: InputStream = FileInputStream(emlFile)
    val props: Properties = System.getProperties()
    val session: Session = Session.getDefaultInstance(props)
    val msg: MimeMessage = MimeMessage(session, source)

    return getEmailText(msg)
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
private fun getEmailText(part: MimePart): String {
    val content: Any = part.content ?: throw ParseException("Could not find content of this email.")
    val contentType: String = part.contentType.split(";")[0].trim().toLowerCase()

    when (contentType) {
        ContentType.TEXT_PLAIN ->
            return (content as String)
        ContentType.MULTIPART_ALT ->
            return getEmailText(
                    (content as MimeMultipart).getBodyPart(0) as MimePart
            )
    // TODO add another content types
        else -> throw NotImplementedError()
    }
}