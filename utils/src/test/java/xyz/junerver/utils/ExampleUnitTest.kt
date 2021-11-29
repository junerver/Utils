package xyz.junerver.utils

import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        assertEquals(0xFF.toByte(),255.toByte())

        val f = File("D:\\ftp_root\\ToastUtil.kt")
        assertEquals(FileUtils.getFileLines(f),47)
        val dirname = FileUtils.getDirName(f)
        println(dirname)
        FileUtils.copy(f, File("D:\\ftp_root\\test\\ToastUtil.kt"))
    }
}