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
 * Use case:
 * 1. Create EmailParser object and call parse().
 * 2. For another parsing, call prepare() and after that call parse().
 */
class EmailParser(var emlFile: File, var mode: ContentParseMode = ContentParseMode.MODE_SIMPLE) {

    /**
     * Contains email's Content-Type header values this library is currently able to parse.
     */
    object ContentType {
        val TEXT_PLAIN = "text/plain"
        val MULTIPART_ALT = "multipart/alternative"
    }

    private var isPrepared = true

    fun prepare(emlFile: File = this.emlFile, mode: ContentParseMode = this.mode): EmailParser {
        this.emlFile = emlFile
        this.mode = mode

        checkMode()

        this.isPrepared = true

        return this
    }

    /**
     * Create MimeMessage from the given source and fetch and decode all needed fields.
     *
     * @return object of Email class.
     * @exception ParseException if fails to parse Date, From or Content fields.
     * @exception NotImplementedError
     */
    fun parse(): Email {
        if (!this.isPrepared) {
            throw ParseException("EmailParser is not prepared.")
        }
        this.isPrepared = false;

        val source: InputStream = FileInputStream(emlFile)
        val props: Properties = System.getProperties()
        val session: Session = Session.getDefaultInstance(props)
        val msg: MimeMessage = MimeMessage(session, source)

        val date: Date = msg.sentDate ?: throw ParseException(getErrorMsg("Date"))

        val from: ArrayList<String> = getAddresses(msg.from)
        if (from.size == 0) {
            throw ParseException(getErrorMsg("From"))
        }
        
        val addresses: Array<Address>? = msg.getRecipients(Message.RecipientType.TO)    
 
        val to: ArrayList<String> = if (addresses == null) { 
            ArrayList<String>() 
        } else { 
            getAddresses(addresses)
        }

        val subject: String = msg.subject ?: ""

        var content: Content = getContent(msg)

        if (this.mode != ContentParseMode.MODE_SIMPLE) {
            content = EmailContentParser(content.body, this.mode).parse()
        }

        return Email(date, from, to, subject, content)
    }

    private fun getErrorMsg(fieldName: String) = "Could not found '$fieldName' field."

    /**
     * Transform addresses from array of Address objects to their string representation.
     *
     * @param addresses array of Address objects.
     * @return ArrayList of addresses' string representation.
     */
    private fun getAddresses(addresses: Array<Address>): ArrayList<String> {
        val addressList = ArrayList<String>()
        addresses.map {
            it as InternetAddress
            if (it.personal == null) {
                addressList.add(it.address)
            } else {
                addressList.add("${it.personal} <${it.address}>")
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
    private fun getContent(part: MimePart): Content {
        val content: Any = part.content ?: throw ParseException(getErrorMsg("Content"))
        var contentType: String = part.contentType.split(";")[0].trim().toLowerCase()

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

    private fun checkMode() {
        if (this.mode < ContentParseMode.MODE_SIMPLE || this.mode > ContentParseMode.MODE_DEEP) {
            throw ParseException("Incorrect parse mode.")
        }
    }
}