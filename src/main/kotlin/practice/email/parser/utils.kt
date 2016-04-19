package practice.email.parser

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
    val n = a.length+1;
    val m = b.length+1;
    var prev = IntArray(n) { it }
    var curr = IntArray(n)
    for (j in 1..m-1) {
        curr[0] = j;
        for (i in 1..n-1) {
            val ins = prev[i] + insCost(b[i-1]);
            val del = curr[i-1] + delCost(a[i-1]);
            val repl = prev[i-1] + replCost(a[i-1], b[j-1]);
            curr[i] = Math.min(Math.min(ins, del), repl);
        }
        val temp = curr
        curr = prev
        prev = temp
    }
    return prev[n-1]
}

fun insCost(c: Char): Int = 1
fun delCost(c: Char): Int = 1
fun replCost(c: Char, d: Char): Int = if (c == d) 0 else 1
