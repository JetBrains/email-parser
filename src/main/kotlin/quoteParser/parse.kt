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

fun parse(emlFile: File): Content {
    val msg: MimeMessage = getMimeMessage(emlFile)
    val emailText: String = getEmailText(msg)
    val isInReplyToHeader = containInReplyToHeader(msg)
    return QuoteParser(isInReplyToEMLHeader = isInReplyToHeader)
            .parse(emailText.lines())
}

fun containInReplyToHeader(msg: MimeMessage) =
        msg.getHeader("In-Reply-To") != null || msg.getHeader("References") != null

fun getEmailText(emlFile: File): String {
    val msg: MimeMessage = getMimeMessage(emlFile)
    return getEmailText(msg)
}

fun getMimeMessage(emlFile: File): MimeMessage {
    val source: InputStream = FileInputStream(emlFile)
    val props: Properties = System.getProperties()
    val session: Session = Session.getDefaultInstance(props)
    val msg: MimeMessage = MimeMessage(session, source)
    return msg
}

/**
 * @param part MimePart to get plain text from.
 * @return string with email content.
 * @exception ParseException if fails to get Content field or email does not contain text/plain content at all.
 */
fun getEmailText(part: MimePart): String {
   val text = searchForContent(part)
    return text ?: throw ParseException("Could not find text/plain part.")
}

private fun searchForContent(part: MimePart): String? {
    val content = part.content ?: throw ParseException("Could not find content of this email.")

    if (part.isMimeType("text/plain")) {
        return content as String
    }
    if (content is MimeMultipart) {
        for (i in 0..content.count - 1) {
            val c = searchForContent(content.getBodyPart(i) as MimePart)
            return c ?: continue
        }
    }
    return null
}