package practice.primes

fun main(args: Array<String>) {
    val enterMessage =
            "\nEnter the number in range from 2 to 1000 to get all prime numbers that are less or equal to it.\nN = "
    var errorMessage = "placeholder error message"
    var n: Int = 2
    var inputString: String?
    var incorrectInput = false
    var gotCLArgument = false

    do {
        if (incorrectInput) {
            println(errorMessage)
            incorrectInput = false
        }
        if (args.size > 0 && !gotCLArgument) {
            inputString = args[0]
            gotCLArgument = true
        } else {
            print(enterMessage)
            inputString = readLine() ?: "null"
        }
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

    println(primes.joinToString(prefix = "Primes: ", postfix = "."))
}

fun getErrorMessage(param: Any): String = when (param) {
    is Int -> "Number $param is not in range 2..1000."
    is String -> """"$param" is not a number."""
    else -> "unknown exception."
}

fun getPrimes(n: Int): IntArray {
    if (n < 2 || n > 1000)
        throw NotInRangeException(getErrorMessage(n))

    val isPrime = BooleanArray(n + 1) { if (it < 2) false else true }
    val n_root = Math.sqrt(n.toDouble()).toInt()

    for (i in 2..n_root) {
        if (isPrime[i]) {
            for (p in i * i..n step i) {
                isPrime[p] = false
            }
        }
    }

    return isPrime.withIndex()
            .map { if (it.value) it.index else 0 }
            .filter { it > 0 }
            .toIntArray()
}
