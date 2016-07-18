package practice.email.executable

import practice.email.clustering.getEstimate
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import java.io.File

private val path = ".${File.separator}src${File.separator}test${File.separator}" +
        "resources${File.separator}contentTests${File.separator}"

private val noArgsMsg = "No arguments are provided. Running with default arguments..."
private val incorrectArgsMsg = "Incorrect number of arguments. You should specify 2 arguments."
private val incorrectFilesMsg = "One or more files are not exist: "

fun main(args: Array<String>) {
    val f1 = "${path}quote_plus_inner_sig.eml"
    val f2 = "${path}only_nested_quotes.eml"
    val eml1: File
    val eml2: File

    when (args.size) {
        0 -> {
            println(noArgsMsg)
            println(f1)
            println(f2)
            println()
            eml1 = File(f1)
            eml2 = File(f2)
        }
        1 -> {
            println(incorrectArgsMsg)
            return
        }
        else -> {
            eml1 = File(args[0])
            eml2 = File(args[1])
        }
    }

    if (!eml1.exists() || !eml2.exists()) {
        println(incorrectFilesMsg)
        if (!eml1.exists()) {
            println(args[0])
        }
        if (!eml2.exists()) {
            println(args[1])
        }
        return
    }

    val content1 = EmailParser(eml1).parse().content.body
    val content2 = EmailParser(eml2).parse().content.body

    val header1 = QuotesHeaderSuggestions.getQuoteHeaderLine(content1)
    val header2 = QuotesHeaderSuggestions.getQuoteHeaderLine(content2)

    println("a = $header1")
    println("b = $header2")

    if (header1 != null && header2 != null) {
        println("Alignment:")
        printAlignment(header1, header2)
    }

    println("estimate = " + getEstimate(eml1, eml2))
}