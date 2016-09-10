package quoteParser.executable

import quoteParser.Content
import quoteParser.QuoteParser
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
        "resources${File.separator}datasets${File.separator}"

fun main(args: Array<String>) {
    val emlDir: File
    if (args.size > 0) {
        emlDir = File(args[0])
        if (emlDir.exists() && emlDir.isDirectory) {
            quoteTest(emlDir)
        } else {
            println("Incorrect path.")
        }
    } else {
        println("Input path to directory with emails as a first command-line argument.")
    }
}

fun quoteTest(emlDir: File) {
    val out = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
            "${pathDatasets}quoteTesting_out.txt"
    ))))

    val emlNums = getABEmlNums()
    var i = 0
    while (i < emlNums.size) {
        try {
            val file = File(emlDir, "${emlNums[i].first}.eml")
            val c = QuoteParser.Builder().build().parse(file)
            writeEml(out, c, emlNums[i])
        } catch(e: Exception) {
            println("${i}.eml gave an error while parsing: ${e.message}")
            println("Skipping...")
            i++
            continue
        } catch(e: Error) {
            println("Some error with eml ${i}.")
            println("Message: ${e.message}")
            println("Skipping...")
            i++
            continue
        }

        if (i % 100 == 0) {
            println("${i} is passed")
        }

        i++
    }
    println("Done.")
    out.close()
}

fun writeEml(out: BufferedWriter, c: Content, p: Pair<Int, String>) {
    out.write("${p.first.toString()}\t\t${p.second}")
    out.newLine()
    c.body.forEach { out.write(it); out.newLine() }
    c.header?.text?.forEach {out.write("> "); out.write(it.toUpperCase()); out.newLine() }
    if (c.quote != null) {

        val xx = c.quote.body.size
        val yy = if (10 > xx) xx else 10
        c.quote.body.slice(0..yy-1).forEach {out.write("> ");  out.write(it); out.newLine() }
    }
    out.newLine()
    out.newLine()
    out.write("X".repeat(150))
    out.newLine()
    out.write("X".repeat(150))
    out.newLine()
}


fun getSLEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(10, "4/4"),
                Pair(20, "4/4"),
                Pair(26, "4/4 split"),
                Pair(75, "4/4 T:sec"),
                Pair(108, "3/4 DT:"),
                Pair(187, "3/4 broken date"),
                Pair(214, "3/4 split D@:"),
                Pair(245, "3/4 + >"),
                Pair(259, "3/4 @DT"),
                Pair(374, "2/4 D:"),
                Pair(427, "4/4+garbage"),
                Pair(484, "text above"),
                Pair(599, "> + gaps"),
                Pair(704, "polish"),
                Pair(800, "4/4 lc split"),
                Pair(880, "garbage skipped"),
                Pair(967, ">wrote: split"),
                Pair(1066, "DT: + > + gaps"),
                Pair(1077, "2/4 D:"),
                Pair(3711, "---sl---"),
                Pair(3997, "Dutch"),
                Pair(4008, "polish reversed"),
                Pair(4169, "> + gaps"),
                Pair(11461, ""),
                Pair(17444, ">>> >>>"),
                Pair(17949, "JSON x2"),
                Pair(17995, ">"),
                Pair(18075, "italian"),
                Pair(18762, "whitespaces"),
                Pair(21086, "DT"),
                Pair(21593, "chinese")
                
        )

fun getMLEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(31,"ML4"),
                Pair(38,"ML5"),
                Pair(43,"ML4"),
                Pair(82,"split"),
                Pair(85,"ML3"),
                Pair(158,""),
                Pair(196,"DT+MC"),
                Pair(210,"ML6"),
                Pair(260,"ML5+sign"),
                Pair(279,"DT+MC+split"),
                Pair(322,"@D+MC"),
                Pair(348,"broken and -T"),
                Pair(3697,">+ML"),
                Pair(3715,">+*ML*"),
                Pair(3926,"organization?"),
                Pair(4298,"+garbage"),
                Pair(5859,""),
                Pair(17326,"+BFM"),
                Pair(17747,"*ML* broken"),
                Pair(18139,"DT+MC skip dates")
        )

fun getABEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(149,""),
                Pair(4237,"split"),
                Pair(5370,"split")
        )

fun getPhraseEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(92,"IRT"),
                Pair(2555,"critical -OM-"),
                Pair(17407,"##--##")
        )

fun getEmptyEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(93, "DT"),
                Pair(102, "ST TR"),
                Pair(146, "D@"),
                Pair(205, ">>>"),
                Pair(1165, "logs + >"),
                Pair(3505, "@:"),
                Pair(20454, ">")
        )

fun getErrorsEmlNums(): Array<Pair<Int, String>> =
        arrayOf(
                Pair(136, "> fp"),
                Pair(166, "fp DT"),
                Pair(1729, "@:"),
                Pair(3549, "@T skip"),
                Pair(3953, "@T skip"),
                Pair(8863, "gaps"),
                Pair(9757, "skip >"),
                Pair(17382, "DT skip"),
                Pair(22912, "logs =(")        
        )
