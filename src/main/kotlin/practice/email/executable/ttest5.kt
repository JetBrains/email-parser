package practice.email.executable


import quoteParser.QuoteParser
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import javax.mail.Session
import javax.mail.internet.MimeMessage

private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"

private val pathEmails = "C:${File.separator}YT${File.separator}"

fun main(args: Array<String>) {
    val out = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}q_h_out.txt"
    ))))
    val inf = BufferedReader(InputStreamReader(FileInputStream(File(
            "${pathDatasets}q_h.txt"
    ))))
    
    inf.lines().filter { it != "" }.map { it.toInt() }.forEach {

        val file = File("${pathEmails}${it}.eml")
        val source: InputStream = FileInputStream(file)
        val props: Properties = System.getProperties()
        val session: Session = Session.getDefaultInstance(props)
        val msg: MimeMessage = MimeMessage(session, source)
        val lines = quoteParser.getEmailText(file).lines()
        val c = QuoteParser().parse(lines)

        val irt = msg.getHeader("In-Reply-To")
        val rfrncs = msg.getHeader("References")
        val dwgrd_irt = msg.getHeader("Downgraded-In-Reply-To")
        val dwgrd_rfrncs = msg.getHeader("Downgraded-References")

        out.write(it.toString())
        out.newLine()
        out.newLine()
        out.write("In-Reply-To: ${irt?.joinToString()}\n")
        out.write("References: ${rfrncs?.joinToString()}\n")
        out.write("Downgraded-In-Reply-To: ${dwgrd_irt?.joinToString()}\n")
        out.write("Downgraded-References: ${dwgrd_rfrncs?.joinToString()}\n")
        out.write("---------------------------------------------\n")
        c.body.forEach { out.write(it); out.newLine() }
        c.header?.text?.forEach {out.write("> "); out.write(it.toUpperCase()); out.newLine() }
        if (c.quote != null) {

            val xx = c.quote.body.size
            val yy = if (10 > xx) xx else 10
            c.quote.body.slice(0..yy-1).forEach {out.write("> ");  out.write(it); out.newLine() }
        }
        out.newLine()
        out.newLine()
        out.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n")
        out.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n")
    }
    
    
    out.close()
    inf.close()
}
