package practice.primes

import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue

class PrimeNumbersSpecifications : Spek() {

    init {
        given("primes calculator") {
            on("numbers in range from 2 to 1000") {
                val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
                val actual = getPrimes(25)
                it("should return all the primes till that given number") {
                    assertArrayEquals(expected, actual)
                }
            }

            // Exception assertion don't work with Spek in JUnit way.
            // So there is used try-catch blocks.
            on("numbers less than 2") {
                var thrown = false
                it("shouldn't do any calculations") {
                    try {
                        getPrimes(1001)
                    } catch (e: NotInRangeException) {
                        thrown = true
                    }
                    assertTrue(thrown)
                }
            }

            on("numbers more than 1000") {
                var thrown = false
                it("shouldn't do any calculations") {
                    try {
                        getPrimes(1001)
                    } catch (e: NotInRangeException) {
                        thrown = true
                    }
                    assertTrue(thrown)
                }
            }
            //---------

            on("numbers in range from 2 to 1000, which are prime") {
                val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
                val actual = getPrimes(29)
                it("must include those numbers in the result") {
                    assertArrayEquals(expected, actual)
                }
            }

            on("number, which is lower bound") {
                val expected = intArrayOf(2)
                val actual = getPrimes(2)
                it("must return that number") {
                    assertArrayEquals(expected, actual)
                }
            }
        }
    }
}
