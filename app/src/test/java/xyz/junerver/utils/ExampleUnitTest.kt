package xyz.junerver.utils

import org.json.JSONObject
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

        assertEquals("sos".upperFirstLetter(), "Sos")
        FileUtils.copy("D:\\ftp_root\\test\\ToastUtil.kt", "D:\\ftp_root\\test1") { scr, dest ->
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

    @Test
    fun testSet() {
        val set = mutableSetOf<Char>()
        set.add('a')
        set.add('b')
        set.add('c')
        set.add('d')
        set.add('a')
        println(set.size)
    }

    @Test
    fun testInfix() {
        val list = emptyList<String>()
        list + "str"

    }

    @Test
    fun testInline() {
//        testClosure {
//            return
//        }
        val counter = fun(): () -> Int {
            var counter = 0
            return fun(): Int {
                return counter++
            }
        }
        val other = counter()
        println(other())
        println(other())
        println(other())

    }

    private inline fun testClosure(test: (String) -> String) {
        println("step 1")
        println(test("step test"))
        println("step 2")
    }

    @Test
    fun testSealed() {
        var c:Color = Color.Red()


        when (c) {
           is Color.Red->{
               println("red")}
           Color.Yellow->{
                println("yellow")
            }
        }
        c = Color.Yellow
        when (c) {
            is Color.Red->{
                println("red")}
            Color.Yellow -> TODO()
        }
    }

    sealed class Color {
        class Red : Color()
        object Yellow:Color()
    }


    @Test
    fun testSwap() {
        val json = JSONObject()
        json.put("a","asdfasf")
        json.put("b", "asdfasf")
        println(json.toString())
    }

    class Person(var name:String)

}


