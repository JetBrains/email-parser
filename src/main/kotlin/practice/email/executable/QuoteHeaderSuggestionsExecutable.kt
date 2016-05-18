package practice.email.executable

import practice.email.clustering.getEstimate
import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import java.io.File

private val path = ".${File.separator}src${File.separator}test${File.separator}" +
        "resources${File.separator}contentTests${File.separator}"

fun main(args: Array<String>) {
    val f1 = "quote_plus_inner_sig.eml"
    val f2 = "only_nested_quotes.eml"

    val eml1 = File("$path$f1")
    val eml2 = File("$path$f2")

    val content1 = EmailParser(eml1).parse().content.body
    val content2 = EmailParser(eml2).parse().content.body

    val header1 = QuotesHeaderSuggestions.getQuoteHeader(content1)
    val header2 = QuotesHeaderSuggestions.getQuoteHeader(content2)

    println("a = $header1")
    println("b = $header2")

    if (header1 != null && header2 != null) {
        println("Alignment:")
        printAlignment(header1, header2)
    }

    println("estimate = " + getEstimate(eml1, eml2))
}