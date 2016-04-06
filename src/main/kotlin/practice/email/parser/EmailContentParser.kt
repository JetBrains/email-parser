package practice.email.parser

import javax.mail.internet.ParseException

/**
 * Use cases: 
 * 1. Create EmailContentParser object and call parse().
 * 2. For another parsing, call prepare() and after that call parse().
 */
class EmailContentParser(var content: String, var mode: Int = EmailContentParser.MODE_ONE) {
    companion object EmailContentParseMode {
        /**
         * Return plain email body content without any parsing.
         */
        val MODE_SIMPLE = 0
        
        /**
         * Parse just outer quote and signature.
         */
        val MODE_ONE = 1

        /**
         * Parse all quotes and signatures recursively.
         */
        val MODE_DEEP = 2
    }

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
    private var sign:  String?
    
    private var isPrepared: Boolean
    
    init {
            checkMode()
            lines = content.split("\n").toTypedArray()
            linesIdx = lines.size - 1
            buffer = Array(lines.size) {""}
            bufferLength = 0
            
            body = ""
            quote = null
            sign = null
        
            isPrepared = true
    }

    /**
     *
     */
    fun prepare(content: String, mode: Int = MODE_ONE): EmailContentParser {
        checkMode()
        this.content = content
        this.mode = mode
        
        lines = content.split("\n").toTypedArray()
        linesIdx = lines.size - 1
        buffer = Array(lines.size) {""}
        bufferLength = 0

        body = ""
        quote = null
        sign = null

        isPrepared = true
        
        return this
    }
    
    /**
     *
     */
    fun parse(): Content {
        if (!isPrepared) {
            throw ParseException("EmailContentParser is not prepared.")
        }
        isPrepared = false;
        
        while (linesIdx > -1 &&
                !lines[linesIdx].trim().equals(SIGNATURE_PATTERN) &&
                !lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
            buffer[bufferLength++] = lines[linesIdx--]
        }

        if (linesIdx <= -1) {
            parseBodyPart()
            return Content(body, quote, sign)
        }

        if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
            parseQuotePart()
            return Content(body, quote, sign)
        }

        if (lines[linesIdx].trim().equals(SIGNATURE_PATTERN)) {
            parseSignaturePart()
            bufferLength = 0
            while ((linesIdx > -1) && !lines[linesIdx].trim().startsWith(QUOTE_PATTERN))  {
                buffer[bufferLength++] = lines[linesIdx--]
            }
            if (linesIdx <= -1) {
                parseBodyPart()
                return Content(body, quote, sign)
            }
            
            if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
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
        while (linesIdx > -1 && !lines[linesIdx].equals("")) {
            if (lines[linesIdx].trim().startsWith(QUOTE_PATTERN)) {
                buffer[bufferLength++] = lines[linesIdx--].substring(1)
            } else {
                buffer[bufferLength++] = lines[linesIdx--]
            }
        }
        val quoteString = reverseAndJoin(buffer, bufferLength)
        if (mode == EmailContentParser.MODE_DEEP) {
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
    
    private fun reverseAndJoin(buffer: Array<String>, bufferLength: Int): String =
        if (bufferLength == 0) {
            ""
        } else {
            val res = buffer.take(bufferLength).toTypedArray()
            res.reverse()
            res.joinToString(separator = "\n")
        }

    private fun checkMode() {
        if (mode < EmailContentParser.MODE_ONE || mode > EmailContentParser.MODE_DEEP) {
            throw ParseException("Incorrect parse mode.")
        }
    }
}