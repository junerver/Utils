package xyz.junerver.utils

/**
 * Description:
 * @author Junerver
 * date: 2021/11/29-9:38
 * Email: junerver@gmail.com
 * Version: v1.0
 */
object TemperatureUtils {
    fun cToF(temp: Float): Float {
        return temp * 9 / 5 + 32
    }

    fun cToK(temp: Float): Float {
        return temp + 273.15f
    }


    fun fToC(temp: Float): Float {
        return (temp - 32) * 5 / 9
    }

    fun fToK(temp: Float): Float {
        return temp + 255.3722222222f
    }


    fun kToC(temp: Float): Float {
        return temp - 273.15f
    }

    fun kToF(temp: Float): Float {
        return temp - 459.67f
    }
}