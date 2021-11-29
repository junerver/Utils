package xyz.junerver.utils

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.experimental.and

/**
 * Description: Byte字节的相关工具
 * @author Junerver
 * date: 2021/11/23-10:48
 * Email: junerver@gmail.com
 * Version: v1.0
 */
object ByteUtils {

    //bf转ba
    @JvmStatic
    fun bytebuffer2ByteArray(buffer: ByteBuffer): ByteArray =
        ByteArray(buffer.capacity()).apply { buffer.get(this) }

    //bf转ba
    fun ByteBuffer.toByteArray(): ByteArray = bytebuffer2ByteArray(this)

    //ba转base64
    fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

    @JvmStatic
    fun byteArray2Base64(ba: ByteArray): String = ba.toBase64()

    //gzip压缩字节流
    @JvmStatic
    fun compresByGzip(data: ByteArray): ByteArray = ByteArrayOutputStream().apply {
        GZIPOutputStream(this).apply {
            write(data)
            finish()
        }
    }.toByteArray()

    //gzip压缩字节流
    fun ByteArray.gzipCompress(): ByteArray = compresByGzip(this)

    //gzip解压缩字节流
    @JvmStatic
    fun uncompressByGzip(data: ByteArray): ByteArray =
        GZIPInputStream(data.inputStream()).use { it.readBytes() }

    //gzip解压缩字节流
    fun ByteArray.uncompressGzip(): ByteArray = uncompressByGzip(this)

    /** 把字byte转换为十六进制的表现形式，如ff  */
    fun byteToHex(byte: Byte) = String.format("%02x", byte.toInt() and 0xFF)

    @JvmStatic
    //读取文件的全部字节
    fun readBytes(file: File): ByteArray = file.readBytes()

    //计算一个BA列表的总长度
    @JvmStatic
    fun countByteArrayList(l: List<ByteArray>): Long = l.fold(0, { a, i -> a + i.size })

    //public static void main(String[] args) {
    //    byte[] bytes = {
    //        (byte) 0xab, 0x01, 0x11
    //    };
    //    String hexStr = bytes2HexStr(bytes);
    //    System.out.println(hexStr);
    //    System.out.println(hexStr2decimal(hexStr));
    //    System.out.println(decimal2fitHex(570));
    //    String adc = "abc";
    //    System.out.println(str2HexString(adc));
    //    System.out.println(bytes2HexStr(adc.getBytes()));
    //}
    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    fun bytes2HexStr(src: ByteArray): String? {
        val builder = StringBuilder()
        if (src.isEmpty()) {
            return ""
        } else {
            val buffer = CharArray(2)
            for (i in src.indices) {
                buffer[0] = Character.forDigit(src[i].toInt().ushr(4) and 0x0F, 16)
                buffer[1] = Character.forDigit((src[i] and 0x0F).toInt(), 16)
                builder.append(buffer)
            }
            return builder.toString().uppercase()
        }
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src 目标数组
     * @param dec 起始位置
     * @param length 长度
     * @return
     */
    fun bytes2HexStr(src: ByteArray, dec: Int, length: Int): String? {
        val temp = ByteArray(length)
        System.arraycopy(src, dec, temp, 0, length)
        return bytes2HexStr(temp)
    }

    /**
     * 16进制字符串转10进制数字
     *
     * @param hex
     * @return
     */
    fun hexStr2decimal(hex: String): Long =
        hex.toLong(16)

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    fun decimal2fitHex(num: Long): String {
        val hex = java.lang.Long.toHexString(num).uppercase()
        return if (hex.length % 2 != 0) {
            "0$hex"
        } else hex.uppercase()
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    fun decimal2fitHex(num: Long, strLength: Int): String? {
        val hexStr = decimal2fitHex(num)
        val stringBuilder = StringBuilder(hexStr)
        while (stringBuilder.length < strLength) {
            stringBuilder.insert(0, '0')
        }
        return stringBuilder.toString()
    }

    fun fitDecimalStr(dicimal: Int, strLength: Int): String? {
        val builder = StringBuilder(dicimal.toString())
        while (builder.length < strLength) {
            builder.insert(0, "0")
        }
        return builder.toString()
    }
}