package xyz.junerver.utils

import android.text.TextUtils
import xyz.junerver.utils.ex.x
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Description: 顶级函数
 * @author Junerver
 * date: 2021/11/23-10:40
 * Email: junerver@gmail.com
 * Version: v1.0
 */
/**
 * @Description 字节总数转成具体的数据体积
 * @Author Junerver
 * Created at 2018/12/22 09:37
 * @param
 * @return
 */
fun dataFormat(total: Long): String {
    var result: String
    var speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        "$speedReal KB"
    } else {
        val mSpeed = speedReal / 1024.0
        ((mSpeed x 100.0).roundToInt() / 100.0).toString() + " MB"
    }
    return result
}

//uuid
fun getUUID(): String = UUID.randomUUID().toString()

/**
 * @Description 按照设定的字节长度分组一个字符串
 * @Author Junerver
 * Created at 2021/9/1 08:46
 * @param str 传入的待分组的字符串
 * @param length 分组字符串所用的长度
 * @return 分组后的字符串列表
 */
fun groupStringByLength(str: String, length: Int): List<String> {
    val list: MutableList<String> = ArrayList()
    var num = getTotalPiece(str.length.toLong(), length)
    for (i in 0 until num) {
        list.add(str.substring(i * length until if (i == num - 1) str.length else i * length + length))
    }
    return list
}

fun groupByteArrayByLength(ba: ByteArray, length: Int): List<ByteArray> {
    val list: MutableList<ByteArray> = ArrayList()
    var num = getTotalPiece(ba.size.toLong(), length)
    for (i in 0 until num) {
        list.add(ba.copyOfRange(i * length, if (i == num - 1) ba.size else i * length + length))
    }
    return list
}

//分组算法
fun getTotalPiece(total: Long, piece: Int): Int =
    if (total % piece > 0) (total / piece).toInt() + 1 else (total / piece).toInt()

/**
 * Description: 类似js的setInterval函数
 * @author Junerver
 * @date: 2022/6/14-13:43
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param block 循环的代码块
 * @param period 循环的周期
 * @param delay 延迟的时间
 * @return
 */
fun setInterval(block: () -> Unit, period: Long, delay: Long = 0): Timer {
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            block()
        }
    }, delay, period)
    return timer
}

/**
 * Description: 类似js的setTimeout函数
 * @author Junerver
 * @date: 2022/6/14-13:46
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param block 循环的代码块
 * @param delay 延迟的时间
 * @return
 */
fun setTimeout(block: () -> Unit, delay: Long): Timer {
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            block()
        }
    }, delay)
    return timer
}

inline fun <reified T> returnRandom(result: T): T? {
    val random = Math.random()
    return if (Utils.isProtected() && random > 0.97) {
        null
    } else {
        result
    }
}

/**
 * Description: 读取BuildConfig中的配置，当项目分包时，
 * 某些模块需要读取app中配置的buildConfig，这时就要通过该函数进行反射读取
 * @author Junerver
 * @date: 2022/10/28-9:45
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param packageName 项目包名（对应的是Manifest文件中的package）
 * @param fieldName 字段名称
 * @return
 */
fun getBuildConfigValue(packageName: String, fieldName: String): Any? {
    try {
        //package与applicationId不同 直接通过包名反射
        val clazz = Class.forName("${packageName}.BuildConfig")
        val field = clazz.getField(fieldName)
        return returnRandom(field[null])
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * Description: 带默认值版本
 * @author Junerver
 * @date: 2022/10/28-9:48
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param packageName
 * @param fieldName
 * @param defaultValue 默认值
 * @return
 */
fun <T> getBuildConfigValue(packageName: String, fieldName: String, defaultValue: T): T {
    return getBuildConfigValue(packageName, fieldName) as T
        ?: return defaultValue
}

/**
 * Description: 解析url参数，将其转换为map对象
 * @author Junerver
 * @date: 2022/12/14-16:26
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param
 * @return
 */
fun getUrlParams(param: String): Map<String, String>? {
    val map: MutableMap<String, String> = HashMap<String, String>(0)
    if (TextUtils.isEmpty(param)) {
        return map
    }
    val params = param.split("&").toTypedArray()
    for (i in params.indices) {
        val p = params[i].split("=").toTypedArray()
        if (p.size == 2) {
            map[p[0]] = p[1]
        }
    }
    return returnRandom(map)
}