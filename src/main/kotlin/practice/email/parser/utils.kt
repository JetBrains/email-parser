package practice.email.parser

import com.sun.xml.internal.fastinfoset.util.StringArray

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

internal fun getEditingDistance(a: String, b: String): Int {
    val firstTokens = getTokens(a)
    val secondTokens = getTokens(b)

    val n = firstTokens.size + 1;
    val m = secondTokens.size + 1;
    var prev = IntArray(n) { it }
    var curr = IntArray(n)
    for (j in 1..m - 1) {
        curr[0] = j;
        for (i in 1..n - 1) {
            val ins = prev[i] + insCost(secondTokens[j-1]);
            val del = curr[i - 1] + delCost(firstTokens[i - 1]);
            val repl = prev[i - 1] + replCost(firstTokens[i - 1], secondTokens[j - 1]);
            curr[i] = Math.min(Math.min(ins, del), repl);
        }
        val temp = curr
        curr = prev
        prev = temp
    }
    return prev[n - 1]
}

private fun insCost(t: Token): Int = 1
private fun delCost(t: Token): Int = 1
private fun replCost(t1: Token, t2: Token): Int = t1.getDifference(t2)

fun getTokens(text: String) = text.split(" ").map { Token(it) }