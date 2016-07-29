package practice.email.parser

import java.util.*


internal fun getEditDistance(firstHeader: String, secondHeader: String): Int {
    val firstTokens = getTokens(firstHeader)
    val secondTokens = getTokens(secondHeader)

    val firstSize = firstTokens.size;
    val secondSize = secondTokens.size;
    var prev = IntArray(firstSize + 1) { 0 }
    for (i in 1..prev.size - 1) {
        prev[i] = prev[i - 1] + firstTokens[i - 1].getDeletionCost()
    }
    var curr = IntArray(firstSize + 1)
    for (j in 1..secondSize) {
        curr[0] = prev[0] + secondTokens[j - 1].getInsertionCost()
        for (i in 1..firstSize) {
            val ins = prev[i] + secondTokens[j - 1].getInsertionCost()
            val del = curr[i - 1] + firstTokens[i - 1].getDeletionCost()
            val repl = prev[i - 1] + firstTokens[i - 1].getDifference(secondTokens[j - 1])
            curr[i] = Math.min(Math.min(ins, del), repl);
        }
        val temp = curr
        curr = prev
        prev = temp
    }
    return prev[firstSize]
}

internal fun getEditDistanceAlignment(firstHeader: String, secondHeader: String): Pair<MutableList<Token>, MutableList<Token>> {
    val firstTokens = getTokens(firstHeader)
    val secondTokens = getTokens(secondHeader)

    val alignmentFirst: MutableList<Token> = ArrayList<Token>()
    val alignmentSecond: MutableList<Token> = ArrayList<Token>()

    var firstSize = firstTokens.size + 1;
    var secondSize = secondTokens.size + 1;
    var distance = Array(secondSize) { IntArray(firstSize) { 0 } }
    distance[0][0] = 0
    for (i in 1..distance[0].size - 1) {
        distance[0][i] = distance[0][i - 1] + firstTokens[i - 1].getDeletionCost()
    }

    for (j in 1..secondSize - 1) {
        distance[j][0] = distance[j - 1][0] + secondTokens[j - 1].getInsertionCost()
        for (i in 1..firstSize - 1) {
            val ins = distance[j - 1][i] + secondTokens[j - 1].getInsertionCost()
            val del = distance[j][i - 1] + firstTokens[i - 1].getDeletionCost()
            val repl = distance[j - 1][i - 1] + firstTokens[i - 1].getDifference(secondTokens[j - 1])
            distance[j][i] = Math.min(Math.min(ins, del), repl);
        }
    }

    --firstSize
    --secondSize

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
            val ins = distance[secondSize - 1][firstSize] + secondTokens[secondSize - 1].getInsertionCost()
            val del = distance[secondSize][firstSize - 1] + firstTokens[firstSize - 1].getDeletionCost()
            val repl = distance[secondSize - 1][firstSize - 1] +
                    firstTokens[firstSize - 1].getDifference(secondTokens[secondSize - 1])

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

    return Pair(alignmentFirst, alignmentSecond)
}

private fun getDash(length: Int): String {
    val sb = StringBuilder()
    for (i in 1..length)
        sb.append("-")
    return sb.toString()
}

/** TODO
 * 1 - determine date related fields
 * 2 - determine last token colon
 */
fun getTokens(text: String) = text.split(Regex("\\s")).filter { !it.equals("") }.map { Token(it) }


