package practice.primes

import org.junit.Test
import org.junit.Assert.assertArrayEquals
import org.junit.Rule
import org.junit.rules.ExpectedException

class PrimeNumbersTests {
    @get:Rule
    val thrown: ExpectedException = ExpectedException.none();

    @Test fun notInRange() {
        val n = 1
        thrown.expect(NotInRangeException::class.java)
        thrown.expectMessage(getErrorMessage(n))
        getPrimes(n)
    }

    @Test fun notInRange2() {
        val n = 1001
        thrown.expect(NotInRangeException::class.java)
        thrown.expectMessage(getErrorMessage(n))
        getPrimes(n)
    }

    @Test fun lastIsPrime() {
        val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
        val actual = getPrimes(29)

        assertArrayEquals(expected, actual)
    }

    @Test fun lastIsPrime2() {
        val expected = intArrayOf(2)
        val actual = getPrimes(2)

        assertArrayEquals(expected, actual)
    }

    @Test fun lastIsNotPrime() {
        val expected = intArrayOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
        val actual = getPrimes(25)

        assertArrayEquals(expected, actual)
    }
}
