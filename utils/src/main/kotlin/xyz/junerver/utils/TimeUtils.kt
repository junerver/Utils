package xyz.junerver.utils

import android.annotation.SuppressLint
import xyz.junerver.utils.TimeUtils.TimeUnit.Companion.currentTimeUnit
import xyz.junerver.utils.TimeUtils.TimeUnit.Companion.timeUnitMillis
import xyz.junerver.utils.ex.safeConvertToLong
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


/**
 * 通用时间类
 * @author czh
 */
object TimeUtils {
    /**
     * 系统计时开始时间
     */
    val SYSTEM_START_DATE = intArrayOf(1970, 0, 1, 0, 0, 0)


    const val LEVEL_YEAR = 0
    const val LEVEL_MONTH = 1
    const val LEVEL_DAY = 2
    const val LEVEL_HOUR = 3
    const val LEVEL_MINUTE = 4
    const val LEVEL_SECOND = 5
    val LEVELS =
        intArrayOf(LEVEL_YEAR, LEVEL_MONTH, LEVEL_DAY, LEVEL_HOUR, LEVEL_MINUTE, LEVEL_SECOND)

    const val NAME_YEAR = "年"
    const val NAME_MONTH = "月"
    const val NAME_DAY = "日"
    const val NAME_HOUR = "时"
    const val NAME_MINUTE = "分"
    const val NAME_SECOND = "秒"
    val LEVEL_NAMES = arrayOf(NAME_YEAR, NAME_MONTH, NAME_DAY, NAME_HOUR, NAME_MINUTE, NAME_SECOND)


    const val YEAR = 0
    const val MONTH = 1
    const val DAY_OF_MONTH = 2
    const val HOUR_OF_DAY = 3
    const val MINUTE = 4
    const val SECOND = 5


    val MIN_TIME_DETAILS = intArrayOf(0, 0, 0)
    val MAX_TIME_DETAILS = intArrayOf(23, 59, 59)


    const val NAME_THE_DAY_BEFORE_YESTERDAY = "前天"
    const val NAME_YESTERDAY = "昨天"
    const val NAME_TODAY = "今天"
    const val NAME_TOMORROW = "明天"
    const val NAME_THE_DAY_AFTER_TOMORROW = "后天"


    const val TYPE_SUNDAY = 0
    const val TYPE_MONDAY = 1
    const val TYPE_TUESDAY = 2
    const val TYPE_WEDNESDAY = 3
    const val TYPE_THURSDAY = 4
    const val TYPE_FRIDAY = 5
    const val TYPE_SATURDAY = 6
    val DAY_OF_WEEK_TYPES = intArrayOf(
        TYPE_SUNDAY,
        TYPE_MONDAY,
        TYPE_TUESDAY,
        TYPE_WEDNESDAY,
        TYPE_THURSDAY,
        TYPE_FRIDAY,
        TYPE_SATURDAY
    )

    const val NAME_SUNDAY = "日"
    const val NAME_MONDAY = "一"
    const val NAME_TUESDAY = "二"
    const val NAME_WEDNESDAY = "三"
    const val NAME_THURSDAY = "四"
    const val NAME_FRIDAY = "五"
    const val NAME_SATURDAY = "六"
    val DAY_OF_WEEK_NAMES = arrayOf(
        NAME_SUNDAY,
        NAME_MONDAY,
        NAME_TUESDAY,
        NAME_WEDNESDAY,
        NAME_THURSDAY,
        NAME_FRIDAY,
        NAME_SATURDAY
    )

    @JvmInline
    value class TimeUnit private constructor(
        val millis: Long
    ) {
        companion object {

            //对应构造值类的函数
            fun millis(millis: Long) = TimeUnit(millis)

            fun seconds(seconds: Long) = TimeUnit(seconds * 1000)

            fun minutes(minutes: Long) = TimeUnit(minutes * 60 * 1000)

            //获取当前的时间TimeUnit
            val currentTimeUnit
                get() = System.currentTimeMillis().timeUnitMillis

            //扩展属性 Long毫秒时间戳转TimeUnit
            val Long.timeUnitMillis
                get() = millis(this)

            //扩展属性 Long秒时间戳转TimeUnit
            val Long.timeUnitSeconds
                get() = seconds(this)
        }
    }

    /**获取日期 年，月， 日 对应值
     * @param date
     * @return
     */
    fun getDateDetail(date: Date): IntArray {
        return getDateDetail(date.time.timeUnitMillis)
    }

    /**获取日期 年，月， 日 对应值
     * @param time 毫秒
     * @return
     */
    fun getDateDetail(time: TimeUnit): IntArray {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = time.millis
        return intArrayOf(
            mCalendar.get(Calendar.YEAR), //0
            mCalendar.get(Calendar.MONTH) + 1, //1
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )//2
    }


    /**
     * 根据生日计算星座
     *
     * @param birthday
     * @return constellation
     */
    fun getConstellation(birthday: Date): String {
        val c = Calendar.getInstance()
        c.time = birthday
        var month = c.get(Calendar.MONTH)
        val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
        val dayArr = intArrayOf(19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 22, 21)
        val starArr = arrayOf(
            "魔羯座",
            "水瓶座",
            "双鱼座",
            "白羊座",
            "金牛座",
            "双子座",
            "巨蟹座",
            "狮子座",
            "处女座",
            "天秤座",
            "天蝎座",
            "射手座"
        )
        if (dayOfMonth > dayArr[month]) {
            month += 1
            if (month == 12) {
                month = 0
            }
        }
        return starArr[month]
    }


    /**
     * 获取日期 年，月， 日， 时， 分， 秒 对应值
     *
     * @param time
     * @return
     */
    fun getWholeDetail(time: TimeUnit): IntArray {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = time.millis
        return intArrayOf(
            mCalendar.get(Calendar.YEAR), //0
            mCalendar.get(Calendar.MONTH) + 1, //1
            mCalendar.get(Calendar.DAY_OF_MONTH), //2
            mCalendar.get(Calendar.HOUR_OF_DAY), //3
            mCalendar.get(Calendar.MINUTE), //4
            mCalendar.get(Calendar.SECOND)//5
        )
    }

    /**
     * 智能时间显示，12:30,昨天，前天...
     *
     * @param date
     * @return
     */
    fun Date.getSmartDate(): String {
        return this.time.getSmartDate()
    }

    /**
     * 智能时间显示，12:30,昨天，前天...
     *
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun Long.getSmartDate(): String {

        val nowDetails = getWholeDetail(currentTimeUnit)
        val smartDetail = getWholeDetail(this.timeUnitMillis)

        var smartDate = ""

        if (nowDetails[0] == smartDetail[0]) {
            if (nowDetails[1] == smartDetail[1]) {
                val time = " " + SimpleDateFormat("HH:mm").format(this)
                val day = (nowDetails[2] - smartDetail[2]).toLong()
                if (day >= 3) {
                    smartDate = smartDetail[2].toString() + "日" + time
                } else if (day >= 2) {
                    smartDate = "前天$time"
                } else if (day >= 1) {
                    smartDate = "昨天$time"
                } else if (day >= 0) {
                    if (0 == nowDetails[HOUR_OF_DAY] - smartDetail[HOUR_OF_DAY]) {
                        val minute =
                            (nowDetails[MINUTE] - smartDetail[MINUTE]).toLong()
                        if (minute < 1) {
                            smartDate = "刚刚"
                        } else if (minute < 31) {
                            smartDate = minute.toString() + "分钟前"
                        } else {
                            smartDate = time
                        }
                    } else {
                        smartDate = time
                    }
                } else if (day >= -1) {
                    smartDate = "明天$time"
                } else if (day >= -2) {
                    smartDate = "后天$time"
                } else {
                    smartDate = smartDetail[2].toString() + "日" + time
                }
            } else {
                smartDate = smartDetail[1].toString() + "月" + smartDetail[2].toString() + "日"
            }
        } else {
            smartDate = smartDetail[0].toString() + "年" + smartDetail[1].toString() + "月"
        }
        return smartDate
    }


    /**
     * 获取智能生日
     *
     * @param birthday
     * @param needYear
     * @return
     */
    fun getSmartBirthday(birthday: TimeUnit, needYear: Boolean = true): String {
        val birthdayDetails = getDateDetail(birthday)
        val nowDetails = getDateDetail(currentTimeUnit)

        val birthdayCalendar = Calendar.getInstance()
        birthdayCalendar.set(birthdayDetails[0], birthdayDetails[1], birthdayDetails[2])

        val nowCalendar = Calendar.getInstance()
        nowCalendar.set(nowDetails[0], nowDetails[1], nowDetails[2])

        val days =
            birthdayCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR)
        if (days < 8) {
            if (days >= 3) {
                return days.toString() + "天后"
            }
            if (days >= 2) {
                return NAME_THE_DAY_AFTER_TOMORROW
            }
            if (days >= 1) {
                return NAME_TOMORROW
            }
            if (days >= 0) {
                return NAME_TODAY
            }
        }

        return if (needYear) {
            birthdayDetails[0].toString() + "年" + birthdayDetails[1] + "月" + birthdayDetails[2] + "日"
        } else birthdayDetails[1].toString() + "月" + birthdayDetails[2] + "日"
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthday: Date): Int {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.time = birthday
        return getAge(
            intArrayOf(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthday: TimeUnit): Int {
        return getAge(getDateDetail(birthday))
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthdayDetail: IntArray?): Int {
        if (birthdayDetail == null || birthdayDetail.size < 3) {
            return 0
        }

        val nowDetails = getDateDetail(currentTimeUnit)

        var age = nowDetails[0] - birthdayDetail[0]

        if (nowDetails[1] < birthdayDetail[1]) {
            age -= 1
        } else if (nowDetails[1] == birthdayDetail[1]) {
            if (nowDetails[2] < birthdayDetail[2]) {
                age -= 1
            }
        }

        return age
    }


    /**根据日期获取指定格式时间
     * @return format yyyy-MM-dd HH:mm:ss
     */

    @SuppressLint("SimpleDateFormat")
    fun Long.formatMillisTimestamp(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return try {
            SimpleDateFormat(format).format(this)
        } catch (e: Exception) {
            "错误的时间戳！"
        }

    }

    /**根据日期获取指定格式时间
     * @return format yyyy-MM-dd HH:mm:ss
     */

    @SuppressLint("SimpleDateFormat")
    fun Date.formatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return try {
            SimpleDateFormat(format).format(this.time)
        } catch (e: Exception) {
            "错误的时间戳！"
        }

    }

    //获取当前时间戳：毫秒
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    //获取当前时间戳：秒
    fun currentTimeSecond(): Long {
        return System.currentTimeMillis() / 1000
    }

    /**
     * Description: 格式化毫秒时间戳字符串
     * @author Junerver
     * @Email: junerver@gmail.com
     * @Version: v1.0
     * @param
     * @return
     */
    fun String.formatMillisTimestamp(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return try {
            val time = this.safeConvertToLong()
            time.formatMillisTimestamp(format)
        } catch (e: Exception) {
            "错误的时间戳！"
        }
    }

    @Deprecated(
        "函数名称不清晰，废弃", ReplaceWith(
            "this.formatMillisTimestamp(format)",
            "xyz.junerver.utils.TimeUtils.timestampToString",
            "xyz.junerver.utils.TimeUtils.formatMillisTimestamp"
        )
    )
    fun String.timestampToString(format: String = "yyyy-MM-dd HH:mm:ss"): String =
        this.formatMillisTimestamp(format)


    /**
     * 获取系统时间
     * @return 字符串完全拼接，可以用于给临时文件命名
     */
    fun getDate(): String {
        val ca = Calendar.getInstance()
        val year = ca.get(Calendar.YEAR)           // 获取年份
        val month = ca.get(Calendar.MONTH)         // 获取月份
        val day = ca.get(Calendar.DATE)            // 获取日
        val minute = ca.get(Calendar.MINUTE)       // 分
        val hour = ca.get(Calendar.HOUR)           // 小时
        val second = ca.get(Calendar.SECOND)       // 秒
        return "" + year + (month + 1) + day + hour + minute + second + (Math.random() * 1000).toInt()
    }

    /**
     * Description: 判断当前时间是否为上午
     * @author Junerver
     * @date: 2022/2/9-10:18
     * @Email: junerver@gmail.com
     * @Version: v1.0
     * @param
     * @return
     */
    fun isMorning(): Boolean {
        return currentTimeMillis().formatMillisTimestamp("HH").toInt() < 12
    }

    /**
     * @Description 将当前的时间转换为数字 用于比较时间是否处于特定时间段内
     * 例如 12:30 ->1230 处于12点到13点之间 1200..1300
     * @Author Junerver
     * Created at 2019-09-11 11:08
     * @param
     * @return
     */
    fun timeToInt(): Int {
        return currentTimeMillis().formatMillisTimestamp("HHmm").toInt()
    }

    /**
     * @Description 将指定的时间转换成今日该时间的时间戳（毫秒）
     * @Author Junerver
     * Created at 2019-09-11 11:11
     * @param time 12:30
     * @return
     */
    fun getTimeInDayByStr(time: String): Long {
        val c = Calendar.getInstance()
        c.time = Date()
        c.set(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH), time.split(':')[0].toInt(), time.split(':')[1].toInt(), 0
        )
        return c.timeInMillis
    }

    /**
     * Description: 毫秒时间转换成时长字符
     * @author Junerver
     * @date: 2022/2/9-10:28
     * @Email: junerver@gmail.com
     * @Version: v1.0
     * @param millis 毫秒级时间长度
     * @param precision 精度，天、时、分、秒、毫秒
     * @return
     */
    @JvmStatic
    fun millis2FitTimeSpan(millis: Long, precision: Int = 1): String? {
        var millis = millis
        var precision = precision
        if (precision <= 0) return null
        precision = min(precision, 5)
        val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
        if (millis == 0L) return "0" + units[precision - 1]
        val sb = StringBuilder()
        if (millis < 0) {
            sb.append("-")
            millis = -millis
        }
        val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
        for (i in 0 until precision) {
            if (millis >= unitLen[i]) {
                val mode = millis / unitLen[i]
                millis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }
}


