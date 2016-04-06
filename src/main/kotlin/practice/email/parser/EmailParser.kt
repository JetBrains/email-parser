package practice.email.parser

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimePart
import javax.mail.internet.ParseException

/**
 * Use case: Create EmailParser object and call
 * one of parseEml() or parseEmlWithContent() methods.
 */
class EmailParser {
//    private val defaultCharset = "UTF-8"

    /**
     * Contains email's Content-Type header values this library is currently able to parse.
     */
    private object ContentType {
        val TEXT_PLAIN = "text/plain"
        val MULTIPART_ALT = "multipart/alternative"
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

    /**
     *
     */
    fun parseEmlWithContent(emlFile: File, mode: Int = EmailContentParser.MODE_SIMPLE): Email {
        checkMode(mode)
        val email = parseEml(emlFile)

        if (mode != EmailContentParser.MODE_SIMPLE) {
            email.content = EmailContentParser(email.content.body, mode).parse()
        }
        return email
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

        val content: Content = getContent(msg)

        return Email(date, from, to, subject, content)
    }

    private fun getErrorMsg(fieldName: String) = "Could not found '$fieldName' field."

    /**
     * Transform addresses from array of Address objects to their string representation.
     *
     * @param addrresses array of Address objects.
     * @return ArrayList of addresses' string representation.
     */
    private fun getAddresses(addresses: Array<Address>): ArrayList<String> {
        val addressList = ArrayList<String>()
        addresses.forEach {
            val addr = it as InternetAddress
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
    // TODO don't like how this method looks. Should to think better about it.
    private fun getContent(part: MimePart): Content {
        val content: Any = part.content ?: throw ParseException(getErrorMsg("Content"))
        var contentType: String = part.contentType.split(";")[0].trim()

        when (contentType) {
            ContentType.TEXT_PLAIN ->
                return Content(
                        (content as String)
                                .replace("\r\n", "\n")
                                .replace("\r", "\n"),
                        null, null
                )
            ContentType.MULTIPART_ALT ->
                return getContent(
                        (content as MimeMultipart).getBodyPart(0) as MimePart
                )
            else -> throw NotImplementedError()
        }
    }

//    private fun getContent(part: MimePart): Content {
//        val content: Any = part.content ?: throw ParseException(getErrorMsg("Content"))
//        var contentType: String = "unknown"
//        var charset: String = "UTF-8"
//
//        // Parse contentType and charset if given
//        for ((i, elem) in part.contentType.split(";").withIndex()) {
//            when (i) {
//                0 -> contentType = elem.trim()
//                else -> {
//                    val s = elem.split("=")
//                    if (s[0].equals("charset")) {
//                        charset = s[1]
//                    }
//                }
//            }
//        }
//
//        when (contentType) {
//            ContentType.TEXT_PLAIN ->
//                return Content(
//                        decodeTo(content as String, charset, defaultCharset)
//                                .replace("\r\n", "\n")
//                                .replace("\r", "\n"),
//                        null, null
//                )
//            ContentType.MULTIPART_ALT ->
//                return getContent(
//                        (content as MimeMultipart).getBodyPart(0) as MimePart
//                )
//            else -> throw NotImplementedError()
//        }
//    }
//
//    /**
//     * Decode the given content from one charset to another.
//     *
//     * @param content string to decode.
//     * @param charsetFrom string representation of source charset.
//     * @param charsetTo string representation of target charset.
//     * @return decoded string.
//     */
//    private fun decodeTo(content: String, charsetFrom: String, charsetTo: String): String =
//            content.toByteArray(charset(charsetFrom)).toString(charset(charsetTo))
    
    private fun checkMode(mode: Int) {
        if (mode < EmailContentParser.MODE_SIMPLE || mode > EmailContentParser.MODE_DEEP) {
            throw ParseException("Incorrect parse mode.")
        }
    }
}