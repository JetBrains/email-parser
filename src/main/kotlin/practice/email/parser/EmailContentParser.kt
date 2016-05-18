package practice.email.parser

import javax.mail.internet.ParseException

enum class ContentParseMode {
    /**
     * Return plain email body content without any parsing.
     */
    MODE_SIMPLE,

    /**
     * Parse just outer quote and signature.
     */
    MODE_ONE,

    /**
     * Parse all quotes and signatures recursively.
     */
    MODE_DEEP
}

/**
 * Use cases:
 * 1. Create EmailContentParser object and call parse().
 * 2. For another parsing, call prepare() and after that call parse().
 */
class EmailContentParser(var content: String, var mode: ContentParseMode = ContentParseMode.MODE_ONE) {

    // Templates for detecting quotes and signatures.
    // Some others will be added later.
    private val SIGNATURE_PATTERN = "--"
    private val QUOTE_PATTERN = ">"


    private var lines: Array<String>
    private var linesIdx: Int
    private var buffer: Array<String>
    private var bufferLength: Int

    private var body: String
    private var quote: Content?
    private var sign: String?

    private var isPrepared: Boolean

    init {
        checkMode()
        this.lines = this.content.split("\n").toTypedArray()
        this.linesIdx = this.lines.size - 1
        this.buffer = Array(this.lines.size) { "" }
        this.bufferLength = 0

        this.body = ""
        this.quote = null
        this.sign = null

        this.isPrepared = true
    }

    /**
     *
     */
    fun prepare(content: String = this.content, mode: ContentParseMode = this.mode): EmailContentParser {
        this.content = content
        this.mode = mode

        checkMode()
        this.lines = this.content.split("\n").toTypedArray()
        this.linesIdx = this.lines.size - 1
        this.buffer = Array(this.lines.size) { "" }
        this.bufferLength = 0

        this.body = ""
        this.quote = null
        this.sign = null

        this.isPrepared = true

        return this
    }

    private fun hasContent() = this.linesIdx > -1
    private fun gotSignature() = this.lines[this.linesIdx].trim().equals(SIGNATURE_PATTERN)
    private fun gotQuote() = this.lines[this.linesIdx].trim().startsWith(QUOTE_PATTERN)

    /**
     *
     */
    fun parse(): Content {
        if (!isPrepared) {
            throw ParseException("EmailContentParser is not prepared.")
        }
        isPrepared = false;

        while (hasContent() && !gotSignature() && !gotQuote()) {
            buffer[bufferLength++] = lines[linesIdx--]
        }

        if (linesIdx <= -1) {
            parseBodyPart()
            return Content(body, quote, sign)
        }

        if (gotQuote()) {
            parseQuotePart()
            return Content(body, quote, sign)
        }

        if (gotSignature()) {
            parseSignaturePart()
            bufferLength = 0
            while (hasContent() && !gotQuote()) {
                buffer[bufferLength++] = lines[linesIdx--]
            }

            if (linesIdx <= -1) {
                parseBodyPart()
                return Content(body, quote, sign)
            }

            if (gotQuote()) {
                parseQuotePart()
                return Content(body, quote, sign)
            }
        }

        // Must never get there.
        throw ParseException("Incorrect message content parsing.")
    }

    private fun parseBodyPart() {
        body = reverseAndJoin(buffer, bufferLength)
        body += "\n"
    }

    private fun parseQuotePart() {
        bufferLength = 0
        buffer[bufferLength++] = lines[linesIdx--].substring(1)
        while (hasContent() && !lines[linesIdx].equals("")) {
            if (gotQuote()) {
                buffer[bufferLength++] = lines[linesIdx--].substring(1)
            } else {
                buffer[bufferLength++] = lines[linesIdx--]
            }
        }
        val quoteString = reverseAndJoin(buffer, bufferLength)
        if (mode == ContentParseMode.MODE_DEEP) {
            quote = EmailContentParser(quoteString, mode).parse()
        } else {
            quote = Content(quoteString, null, null)
        }
        body = lines.take(linesIdx + 1).joinToString(separator = "\n")
        body += "\n"
    }

    private fun parseSignaturePart() {
        buffer[bufferLength++] = lines[linesIdx--]
        sign = reverseAndJoin(buffer, bufferLength)
    }

    private fun checkMode() {
        if (this.mode < ContentParseMode.MODE_ONE || this.mode > ContentParseMode.MODE_DEEP) {
            throw ParseException("Incorrect parse mode.")
        }
    }
}

internal fun reverseAndJoin(buffer: Array<String>, bufferLength: Int): String =
        if (bufferLength == 0) {
            ""
        } else {
            val res = buffer.take(bufferLength).toTypedArray()
            res.reverse()
            res.joinToString(separator = "\n")
        }
