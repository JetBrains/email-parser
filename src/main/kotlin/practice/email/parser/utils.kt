package practice.email.parser

import practice.email.parser.QuotesHeaderSuggestions.getQuoteHeader
import java.io.File

enum class ContentParseMode {
    /**
     * Return plain email body content without any parsing.
     */
    MODE_SIMPLE,

    /**
     * Parse just outer quote and signature.
     */
    MODE_ONE,

    /**
     * Parse all quotes and signatures recursively.
     */
    MODE_DEEP
}

internal fun reverseAndJoin(buffer: Array<String>, bufferLength: Int): String =
        if (bufferLength == 0) {
            ""
        } else {
            val res = buffer.take(bufferLength).toTypedArray()
            res.reverse()
            res.joinToString(separator = "\n")
        }

private fun insCost(t: Token): Int = Token.INSERTION_COST
private fun delCost(t: Token): Int = Token.DELETION_COST
private fun replCost(t1: Token, t2: Token): Int = t1.getDifference(t2)

internal fun getEditingDistance(firstHeader: String, secondHeader: String): Int {
    val firstTokens = getTokens(firstHeader)
    val secondTokens = getTokens(secondHeader)

    val firstSize = firstTokens.size;
    val secondSize = secondTokens.size;
    var prev = IntArray(firstSize + 1) { it * Token.INSERTION_COST }
    var curr = IntArray(firstSize + 1)
    for (j in 1..secondSize) {
        curr[0] = j * Token.DELETION_COST
        for (i in 1..firstSize) {
            val ins = prev[i] + insCost(secondTokens[j - 1]);
            val del = curr[i - 1] + delCost(firstTokens[i - 1]);
            val repl = prev[i - 1] + replCost(firstTokens[i - 1], secondTokens[j - 1]);
            curr[i] = Math.min(Math.min(ins, del), repl);
        }
        val temp = curr
        curr = prev
        prev = temp
    }
    return prev[firstSize]
}

internal fun getEditingDistance2(firstHeader: String, secondHeader: String,
                                 alignmentFirst: MutableList<Token>, alignmentSecond: MutableList<Token>): Int {
    val firstTokens = getTokens(firstHeader)
    val secondTokens = getTokens(secondHeader)

    var firstSize = firstTokens.size + 1;
    var secondSize = secondTokens.size + 1;
    var distance = Array(secondSize) { IntArray(firstSize) { 0 } }
    for (i in distance[0].indices) {
        distance[0][i] = i * Token.INSERTION_COST
    }

    for (j in 1..secondSize - 1) {
        distance[j][0] = j * Token.DELETION_COST
        for (i in 1..firstSize - 1) {
            val ins = distance[j - 1][i] + insCost(secondTokens[j - 1]);
            val del = distance[j][i - 1] + delCost(firstTokens[i - 1]);
            val repl = distance[j - 1][i - 1] + replCost(firstTokens[i - 1], secondTokens[j - 1]);
            distance[j][i] = Math.min(Math.min(ins, del), repl);
        }
    }

    val firstDistanceIndex = --firstSize
    val secondDistanceIndex = --secondSize

    while (firstSize > 0 || secondSize > 0) {
        if (firstSize == 0) {
            val dash = getDash(secondTokens.elementAt(secondSize - 1).text.length)
            alignmentFirst.add(0, Token(dash))
            alignmentSecond.add(0, secondTokens.elementAt(secondSize - 1))
            secondSize--
        } else if (secondSize == 0) {
            val dash = getDash(firstTokens.elementAt(firstSize - 1).text.length)
            alignmentFirst.add(0, firstTokens.elementAt(firstSize - 1))
            alignmentSecond.add(0, Token(dash))
            firstSize--
        } else {
            val ins = distance[secondSize - 1][firstSize] + insCost(secondTokens[secondSize - 1]);
            val del = distance[secondSize][firstSize - 1] + delCost(firstTokens[firstSize - 1]);
            val repl = distance[secondSize - 1][firstSize - 1] + 
                    replCost(firstTokens[firstSize - 1], secondTokens[secondSize - 1]);

            if (ins <= del && ins <= repl) {
                val dash = getDash(secondTokens.elementAt(secondSize - 1).text.length)
                alignmentFirst.add(0, Token(dash))
                alignmentSecond.add(0, secondTokens.elementAt(secondSize - 1))
                secondSize--
            } else {
                if (del <= repl) {
                    val dash = getDash(firstTokens.elementAt(firstSize - 1).text.length)
                    alignmentFirst.add(0, firstTokens.elementAt(firstSize - 1))
                    alignmentSecond.add(0, Token(dash))
                    firstSize--
                } else {
                    var t1: Token = firstTokens.elementAt(firstSize - 1)
                    var t2: Token = secondTokens.elementAt(secondSize - 1)
                    
                    if (t1 == t2) {
                        t1.text = "+${t1.text}"
                        t2.text = "+${t2.text}"
                    }
                    
                    alignmentFirst.add(0, t1)
                    alignmentSecond.add(0, t2)
                    
                    firstSize--
                    secondSize--
                }
            }
        }
    }

    return distance[secondDistanceIndex][firstDistanceIndex]
}

private fun getDash(length: Int): String {
    val sb = StringBuilder()
    for (i in 1..length)
        sb.append("-")
    return sb.toString()
}

private fun getTokens(text: String) = text.split(Regex("\\s")).filter { !it.equals("") }.map { Token(it) }

fun getEstimate(eml1: File, eml2: File): Int {
    val content1 = EmailParser(eml1).parse().content.body
    val content2 = EmailParser(eml2).parse().content.body

    val header1 = getQuoteHeader(content1)
    val header2 = getQuoteHeader(content2)

    return when {
        header1 == null && header2 == null -> 0
        header1 == null || header2 == null -> Int.MAX_VALUE
        else -> getEditingDistance(header1, header2)
    }
}
