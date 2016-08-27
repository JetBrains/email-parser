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
private val pathEmails = "D:${File.separator}YT${File.separator}"


private val FILTER_STRING = "##- Please type your reply above this line -##"

private val EMAILS_COUNT = 23055

fun main(args: Array<String>) {
    val qnonh = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}q_nonh.txt"
    ))))
    val nonqh = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}nonq_h.txt"
    ))))
    val down = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}downgraded.txt"
    ))))
    var qnonhCount = 0
    var nonqhCount = 0
    var downCount = 0
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
        val dwgrd_irt = msg.getHeader("Downgraded-In-Reply-To")
        val dwgrd_rfrncs = msg.getHeader("Downgraded-References")

        if (header != null && irt == null && rfrncs== null && dwgrd_irt== null&& dwgrd_rfrncs== null) {
            qnonh.write(i.toString())
            qnonh.newLine()
            qnonhCount++
        }
        if (header == null && (irt != null || rfrncs!= null || dwgrd_irt!= null || dwgrd_rfrncs != null)) {
            
            nonqh.write(i.toString())
            nonqh.newLine()
            nonqhCount++
        }
        if (dwgrd_irt!= null || dwgrd_rfrncs != null) {
            down.write(i.toString())
            down.newLine()
            downCount++
        }

        if (i % 100 == 0) {
            println("${i} is passed")
        }
    }

    qnonh.close()
    nonqh.close()
    down.close()

    println("Done.")
    println("${qnonhCount} emails with quote has no headers.")
    println("${nonqhCount} emails with headers has no quotes.")
    println("${downCount} emails with downground headers.")
}


