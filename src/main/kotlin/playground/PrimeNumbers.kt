package playground

fun main(args: Array<String>) {
    val n = readLine()!!.toInt()

    if (n !in 2..1000) {
        println("Number not in range from 2 to 1000.")
        return
    }
    val primes: IntArray = getPrimes(n)
    printPrimes(primes)
}

internal fun getPrimes(n: Int): IntArray {
    if (n < 2 || n > 1000)
        return intArrayOf()

    val isPrime = BooleanArray(n + 1) { if (it < 2) false else true }
    val n_root = Math.sqrt(n.toDouble()).toInt()

    for (i in 2..n_root) {
        if (isPrime[i]) {
            for (p in i * i..n step i) {
                isPrime[p] = false
            }
        }
    }

    val primesAmount = isPrime.count { it }
    var prime = 0
    return IntArray(primesAmount) {
        while (!(isPrime[prime]) && prime < isPrime.size)
            ++prime
        prime++
    }
}

private fun printPrimes(primes: IntArray) {
    for (i in primes.indices) {
        if (i != primes.size - 1) {
            print("${primes[i]}, ")
        } else {
            print(primes[i])
        }
    }
    println()
}