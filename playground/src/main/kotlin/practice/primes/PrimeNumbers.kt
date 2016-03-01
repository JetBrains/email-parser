package practice.primes

fun main(args: Array<String>) {
    val enterMessage =
            "\nEnter the number in range from 2 to 1000 to get all prime numbers that are less or equal to it.\nN = "
    var errorMessage = "placeholder error message"
    var n: Int = 2
    var inputString: String?
    var incorrectInput = false

    do {
        if (incorrectInput) {
            println(errorMessage)
            incorrectInput = false
        }
        print(enterMessage)
        inputString = readLine() ?: "null"
        try {
            n = inputString.toInt()
            if (n !in 2..1000) {
                errorMessage = getErrorMessage(n)
                incorrectInput = true
            }
        } catch (e: NumberFormatException) {
            errorMessage = getErrorMessage(inputString)
            incorrectInput = true
        }
    } while (incorrectInput)

    val primes: IntArray = getPrimes(n)
    printPrimes(primes)
}

fun getErrorMessage(n: Int): String = "Number $n is not in range 2..1000."

fun getErrorMessage(str: String?): String = """"$str" is not a number."""

fun getPrimes(n: Int): IntArray {
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
