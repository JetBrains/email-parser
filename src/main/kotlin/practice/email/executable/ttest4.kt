package practice.email.executable

import practice.email.parser.Email
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.preprocess
import quoteParser.QuoteParser
import quoteParser.getEmailText
import java.io.*
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import javax.mail.Session
import javax.mail.internet.MimeMessage

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"
private val pathEmails = "C:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 23055

fun main(args: Array<String>) {
    val qh = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}q_h.txt"
    ))))
    val nonqnonh = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}nonq_nonh.txt"
    ))))
    var qhCount = 0
    var nonqnonhCount = 0
    for (i in 0.. EMAILS_COUNT - 1) {

        val header: List<String>?
        val msg: MimeMessage
        val email: List<String>
        try {
            val file = File("${pathEmails}${i}.eml")
            email = getEmailText(file).lines()
            val source: InputStream = FileInputStream(file)
            val props: Properties = System.getProperties()
            val session: Session = Session.getDefaultInstance(props)
            msg = MimeMessage(session, source)
            if (!email[0].trim().equals(FILTER_STRING)) {
                val H = QuoteParser().parse(email).header
                if (H != null && H.text.isEmpty()) {
                    header = listOf(email[H.startIndex])
                } else {
                    header = H?.text
                }
            } else {
                continue
            }

        } catch(e: Exception) {
            println("${i}.eml gave an error while parsing: ${e.message}")
            println("Skipping...")
            continue
        } catch(e: Error) {
            println("Some error with eml ${i}.")
            println("Message: ${e.message}")
            println("Skipping...")
            continue
        }

        val irt = msg.getHeader("In-Reply-To")
        val rfrncs = msg.getHeader("References")

        if (header == null && irt == null && rfrncs== null) {
            nonqnonh.write(i.toString())
            nonqnonh.newLine()
            nonqnonhCount++
        }
        if (header != null && (irt != null || rfrncs!= null)) {
            
            qh.write(i.toString())
            qh.newLine()
            qhCount++
        }
        if (i % 100 == 0) {
            println("${i} is passed")
        }
    }

    qh.close()
    nonqnonh.close()

    println("Done.")
    println("${qhCount} emails with quote and headers.")
    println("${nonqnonhCount} emails without headers and quote.")
}


