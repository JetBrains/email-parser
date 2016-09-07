package practice.email.parser

import java.util.*

/**
 * Data class for storing some parsed email headers and body content.
 */
data class Email(val date: Date,
                 val from: ArrayList<String>,
                 val to: ArrayList<String>,
                 val subject: String,
                 var content: Content) {

    private val forkSeparator = "+---"
    private val leafSeparator = "|___"

    override fun toString(): String {
        val builder = StringBuilder()
        getHeaders(builder)
        builder.append("-----------------------------------\n")
        builder.append(content)
        return builder.toString()
    }

    fun getTreeRepresentation(): String {
        val builder = StringBuilder()
        getHeaders(builder)
        getContent(content, StringBuilder(""), builder)
        return builder.toString()
    }

    private fun getHeaders(builder: StringBuilder) {
        builder.append("Date: $date\n")
        for (f in from) {
            builder.append("From: $f\n")
        }
        for (t in to) {
            builder.append("To: $t\n")
        }
        builder.append("Subject: $subject\n")
    }

    private fun getContent(content: Content, paddingBuilder: StringBuilder, resultBuilder: StringBuilder) {
        val bodyPaddingBuilder = StringBuilder(paddingBuilder)
        var noSignature = content.signature == null
        if (noSignature) {
            bodyPaddingBuilder.append(" ")
        } else {
            bodyPaddingBuilder.append("|")
        }

        resultBuilder.append(paddingBuilder.toString())
        resultBuilder.append(
                if (noSignature)
                    leafSeparator
                else
                    forkSeparator
        )
        resultBuilder.append("\n")

        val bodyPadding = bodyPaddingBuilder.toString()
        val body = content.body.split("\n").toTypedArray()
        for (line in body) {
            resultBuilder.append(bodyPadding)
            resultBuilder.append("$line\n")
        }

        if (content.quote != null) {
            getContent(content.quote, bodyPaddingBuilder.append("   "), resultBuilder)
        }

        if (content.signature != null) {

            resultBuilder.append(paddingBuilder.toString())
            resultBuilder.append("$leafSeparator\n")

            val signPadding = paddingBuilder.append(" ").toString()
            val sign = content.signature.split("\n").toTypedArray()
            for (line in sign) {
                resultBuilder.append(signPadding)
                resultBuilder.append("$line\n")
            }
        }
    }
}