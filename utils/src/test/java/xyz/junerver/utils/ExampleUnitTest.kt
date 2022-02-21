package xyz.junerver.utils

import org.junit.Test

import org.junit.Assert.*
import xyz.junerver.utils.TimeUtils.TimeUnit.Companion.timeUnitSeconds
import xyz.junerver.utils.TimeUtils.formatMillisTimestamp
import xyz.junerver.utils.TimeUtils.isMorning
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

        assertEquals(0xFF.toByte(), 255.toByte())

        val f = File("D:\\ftp_root\\ToastUtil.kt")
        assertEquals(FileUtils.getFileLines(f), 47)
        val dirname = FileUtils.getDirName(f)
        println(dirname)

        FileUtils.copy("D:\\ftp_root\\test\\ToastUtil.kt", "D:\\ftp_root\\test1") { scr, dest ->
            println("存在目标文件 $scr _ $dest")
            true
        }

        FileUtils.copy("","") { file: File, file1: File ->
            true
        }
    }

    @Test
    fun testTimeUtil() {
        val time = "1644372399000"
        assertEquals("2022-02-09 10:06:39",time.formatMillisTimestamp())
        println(TimeUtils.millis2FitTimeSpan(100000000,4))
        println(TimeUtils.getDate())
        println(isMorning())
        println(TimeUtils.getAge(1297217199L.timeUnitSeconds))
        println(TimeUtils.TimeUnit.currentTimeUnit)
    }
}