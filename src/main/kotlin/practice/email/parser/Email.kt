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

    override fun toString(): String {
        val builder = StringBuilder()

        builder.append("Date: $date\n")
        for (f in from) {
            builder.append("From: $f\n")
        }
        for (t in to) {
            builder.append("To: $t\n")
        }
        builder.append("Subject: $subject\n")
        builder.append("-----------------------------------\n")
        builder.append(content)
        return builder.toString()
    }
}