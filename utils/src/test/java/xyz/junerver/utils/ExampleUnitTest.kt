package xyz.junerver.utils

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import xyz.junerver.utils.TimeUtils.TimeUnit.Companion.timeUnitSeconds
import xyz.junerver.utils.TimeUtils.formatMillisTimestamp
import xyz.junerver.utils.TimeUtils.isMorning
import xyz.junerver.utils.ex.isEmail
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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

        FileUtils.copy("", "") { file: File, file1: File ->
            true
        }
    }

    @Test
    fun testRadix() {
        val a = 0b001
        val b = 0b100
        val c = a or b
        // 数字以二进制形式输出为字符串 Integer.toBinaryString(mAdminState)
        println(c.toString(2))
//        将二进制字符串转成二进制数字
        val d = Integer.valueOf("111",2)
        println(d.toString(2))
        val e = "1001".toInt(2)
        println(e)
    }

    @Test
    fun testTimeUtil() {
        val time = "1644372399000"
        assertEquals("2022-02-09 10:06:39", time.formatMillisTimestamp())
        println(TimeUtils.millis2FitTimeSpan(100000000, 4))
        println(TimeUtils.getDate())
        println(isMorning())
        println(TimeUtils.getAge(1297217199L.timeUnitSeconds))
        println(TimeUtils.TimeUnit.currentTimeUnit)
    }

    @Test
    fun testOpAndInfix() {
        val list = mutableListOf("1", "2")
        list += "3"
        list + "3"

    }

    @Test
    fun testIsEmail() {
        println("c@on.tact@wired.me.uk".isEmail())
        println("con.tact@wired.me.uk".isEmail())
        println("contact@wired.me.uk".isEmail())
        println("...@wired.me.uk".isEmail())
    }

    @Test
    fun testJsonBuilder(){
        val jo = json {
            "name" to "zhangsan"
            val arr = arrayOf(1,2,3,4)
            "keys" to arr.map {
                json {
                    "key" to it
                    "index" to "学号：$it"
                }
            }.toTypedArray()
            "info" to json {
                "email" to "xxxx@mail.com"
                "age" to 18
                "sex" to '1'
            }
            "o" to {
                "2" to "3"
                "arr" to listOf<String>("1","2","3")
            }
        }
        //验证字段名称
        assertEquals("zhangsan",jo.getString("name"))
        //验证JSONArray的长度
        assertEquals(4, jo.optJSONArray("keys")?.length() ?: 0)
        assertEquals(true,jo.get("info") is JSONObject)
    }

    @Test
    fun testJsonArray(){
        //创建root为 array的json
        val ja = JSONArray(listOf(
            json{"name" to "张三"},
            json{"name" to "李四"},
            json{"name" to "王五"},
        ))
        assertEquals(3,ja.length())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testValue() {
        val t3 = measureTime {
            repeat(100) {
                Normal("1000")
            }
        }
        val t5 = measureTime {
            repeat(100) {
                Normal2("1000")
            }
        }
        val t6 = measureTime {
            repeat(100) {
                Normal3("1000")
            }
        }

        println("$t3 $t5 $t6")
    }

    class Normal(val time: String)

    data class Normal3(val time: String)

    @JvmInline
    value class Normal2(val time: String)

    @Test
    fun testJSON() {
        val json = JSONObject()
        json.put("a","asdfasf")
        json.put("b", "asdfasf")
        println(json.toString())
    }

}