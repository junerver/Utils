package xyz.junerver.utils

import org.junit.Test

import org.junit.Assert.*
import xyz.junerver.utils.ex.upperFirstLetter
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

//        println(SslUtils.getSslSocketFactory().sslSocketFactory)

        assertEquals("sos".upperFirstLetter(),"Sos")
        FileUtils.copy("D:\\ftp_root\\test\\ToastUtil.kt","D:\\ftp_root\\test1") { scr, dest ->
            println("存在目标文件 $scr _ $dest")
            true
        }

        println(FileUtils.getFileExtension("D:\\ftp_root\\test\\ToastUtil.kt"))
        println(File("D:\\ftp_root\\test\\ToastUtil.kt"))
        println(File("D:\\ftp_root\\test\\ToastUtil.kt").lastModified())

//        val l =
//            FileUtils.getFileLength("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
//        println(l)
    }
}


