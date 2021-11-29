package xyz.junerver.utils

/**
 * Description:
 *
 * @author Junerver
 * date: 2021/11/23-15:07
 * Email: junerver@gmail.com
 * Version: v1.0
 */
object StringUtils {
    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return `true`: yes<br></br> `false`: no
     */
    @JvmStatic
    fun isSpace(s: String?): Boolean = s.isNullOrBlank()
}