import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertEquals
import playground.getPrimes

class Specifications: Spek() {
    init {
        given("primes calculator") {
            on("numbers in range from 2 to 1000") {
                val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
                val actual = getPrimes(25)
                it("should return all the primes till that given number") {
                    for(i in expected.indices) {
                        assertEquals(expected[i], actual[i]);
                    }
                }
            }
            on("numbers less than 2") {
                val expected = intArrayOf()
                val actual = getPrimes(1)
                it("shouldn't do any calulations") {
                    for(i in expected.indices) {
                        assertEquals(expected[i], actual[i]);
                    }
                }
            }
            on("numbers more than 1000") {
                val expected = intArrayOf()
                val actual = getPrimes(1001)
                it("shouldn't do any calulations") {
                    for(i in expected.indices) {
                        assertEquals(expected[i], actual[i]);
                    }
                }
            }
            on("numbers in range from 2 to 1000, which are prime") {
                val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
                val actual = getPrimes(29)
                it("must include those numbers in the result") {
                    for(i in expected.indices) {
                        assertEquals(expected[i], actual[i]);
                    }
                }
            }
            on("number, which is lower bound") {
                val expected = intArrayOf(2)
                val actual = getPrimes(2)
                it("must return that number") {
                    for(i in expected.indices) {
                        assertEquals(expected[i], actual[i]);
                    }
                }
            }

        }
    }
}
