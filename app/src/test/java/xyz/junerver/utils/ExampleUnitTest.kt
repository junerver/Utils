package xyz.junerver.utils

import org.junit.Test

import org.junit.Assert.*
import xyz.junerver.utils.ex.upperFirstLetter

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

//        println(SslUtils.getSslSocketFactory().sslSocketFactory)

        assertEquals("sos".upperFirstLetter(),"Sos")

    }
}