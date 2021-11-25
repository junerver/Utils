package com.junerver.utils


import com.junerver.corelib.padLeft
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.UnsupportedEncodingException
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**MD5加密工具类
 */

/**
 * 对字符串进行 MD5 加密
 *
 * @param str
 * 待加密字符串
 * @return 加密后字符串
 */
fun MD5(str: String): String {
    if (str.isEmpty()) {
        return ""
    }
    val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    var md5: MessageDigest? = null
    try {
        md5 = MessageDigest.getInstance("MD5")
        md5!!.update(str.toByteArray(charset("UTF-8")))
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        throw RuntimeException(e.message)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        throw RuntimeException(e.message)
    }
    val encodedValue = md5.digest()
    val j = encodedValue.size
    val finalValue = CharArray(j * 2)
    var k = 0
    for (i in 0 until j) {
        val encoded = encodedValue[i]
        finalValue[k++] = hexDigits[encoded.toInt() shr 4 and 0xf]
        finalValue[k++] = hexDigits[encoded.toInt() and 0xf]
    }

    return String(finalValue)
}

fun String.md5() = MD5(this)

/**
 * Description: 获取文件的MD5值
 * @author Junerver
 * @date: 2021/11/23-10:46
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param
 * @return
 */
fun MD5(file: File): String {
    if (file.length() > 2 * 1024 * 1024 * 1024) {
        throw IllegalArgumentException("too large!")
    }
    val digest = MessageDigest.getInstance("MD5")
    digest.update(file.readBytes())
    return BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
}

fun File.md5() = MD5(this)
