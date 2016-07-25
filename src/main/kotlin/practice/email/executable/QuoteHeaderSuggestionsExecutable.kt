package practice.email.executable

import practice.email.clustering.getEstimate
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.getEditDistance
import practice.email.parser.preprocess
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

    val headerList1 = QuotesHeaderSuggestions.getQuoteHeader(content1)
    val headerList2 = QuotesHeaderSuggestions.getQuoteHeader(content2)

    val headerLine1 = if (headerList1 == null)
        null
    else
        preprocess(headerList1)

    val headerLine2 = if (headerList2 == null)
        null
    else
        preprocess(headerList2)

    println("a = $headerLine1")
    println("b = $headerLine2")

    if (headerLine1 != null && headerLine2 != null) {
        println("Alignment:")
        printAlignment(headerLine1, headerLine2)
    }

   // println("estimate = " + getEstimate(eml1, eml2))
}