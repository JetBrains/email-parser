import org.junit.Test
import org.junit.Assert.assertEquals
import playground.getPrimes

class Tests {

    @Test fun notInRange() {
        val expected = intArrayOf()
        val actual = getPrimes(1)

        for(i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test fun notInRange2() {
        val expected = intArrayOf()
        val actual = getPrimes(1001)

        for(i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test fun lastIsPrime() {
        val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
        val actual = getPrimes(29)

        for(i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test fun lastIsPrime2() {
        val expected = intArrayOf(2)
        val actual = getPrimes(2)

        for(i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test fun lastIsNotPrime() {
        val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
        val actual = getPrimes(25)

        for(i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }
}
