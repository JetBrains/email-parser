package playground

fun main(args: Array<String>) {
    val n = readLine()!!.toInt()

    if (n !in 2..1000) {
        println("Number not in range from 2 to 1000.")
        return
    }
    val isPrime: BooleanArray = getPrimes(n)
    printPrimes(n, isPrime)

}

private fun getPrimes(n: Int): BooleanArray {
    val isPrime = BooleanArray(n + 1) { true }
    val n_root = Math.sqrt(n.toDouble()).toInt()

    for (i in 2..n_root) {
        if (isPrime[i]) {
            for (p in i * i..n step i) {
                isPrime[p] = false
            }
        }
    }

    return isPrime
}

private fun printPrimes(n: Int, isPrime: BooleanArray) {
    val numbers = IntArray(n - 1) { it + 2 }

    print(2)
    var first = true
    for (i in numbers) {
        if (isPrime[i] && !first) {
            print(", $i")
        }
        first = false
    }
    println()
}