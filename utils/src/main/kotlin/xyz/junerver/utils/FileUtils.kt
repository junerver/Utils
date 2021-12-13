package xyz.junerver.utils

import xyz.junerver.utils.Utils.getApp
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.os.StatFs
import java.io.*
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.experimental.and

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/05/03
 * desc  : utils about file
</pre> *
 */
object FileUtils {
    /**
    * Description: kt在 1.4 之后增加了对SAM的支持，只需对 [interface] 增加 [fun] 关键字即可
    * @author Junerver
    * @date: 2021/11/30-9:37
    * @Email: junerver@gmail.com
    * @Version: v1.0
    * @param
    * @return
    */
    fun interface OnReplaceListener {
        fun onReplace(srcFile: File, destFile: File): Boolean
    }

    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * 根据传入文件路径返回File对象
     *
     * @param filePath 文件路径
     * @return the file
     */
    @JvmStatic
    fun getFileByPath(filePath: String?): File? {
        return if (UtilsBridge.isSpace(filePath)) null else File(filePath)
    }

    /**
     * 根据File对象判断文件是否存在
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isFileExists(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            true
        } else isFileExists(file.absolutePath)
    }

    /**
     * 根据文件路径判断文件是否存在
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String?): Boolean {
        val file = getFileByPath(filePath) ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath)
    }

    //api > 29 时检查文件是否存在
    private fun isFileExistsApi29(filePath: String?): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri = Uri.parse(filePath)
                val cr = getApp().contentResolver
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }

    /**
     * 文件重命名(路径)
     *
     * @param filePath 文件路径
     * @param newName  新名字
     * @return 是否成功
     */
    fun rename(filePath: String?, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * 文件重命名(File对象)
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null || !file.exists()) return false
        // the new name is space then return false
        if (UtilsBridge.isSpace(newName)) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return (!newFile.exists()
                && file.renameTo(newFile))
    }

    /**
     * 根据路径判断是否为目录
     *
     * @param dirPath The path of directory.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(dirPath: String?): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 根据File对象判断是否为目录
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * 根据路径判断是否为文件
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(filePath: String?): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 根据File对象判断是否为文件
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * 创建一个目录，如果存在该目录则 do nothing
     *
     * @param dirPath The path of directory.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 创建一个目录（File），如果存在该目录则 do nothing
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 创建一个文件，如果存在该文件则 do nothing
     *
     * @param filePath The path of file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 创建一个文件（File），如果存在该文件则 do nothing
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 创建一个文件，如果存在该文件则先删除再创建
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 创建一个文件（File），如果存在该文件则先删除再创建
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 拷贝目录或者文件
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(
        srcPath: String?,
        destPath: String?
    ): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * 拷贝目录或者文件
     *
     * @param srcPath  源目录or文件
     * @param destPath 目标目录or文件
     * @param listener 当目标目录or文件存在同名文件时是否替换监听器
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(
        srcPath: String?,
        destPath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }
    /**
     * Copy the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(
        src: File?,
        dest: File?,
        listener: OnReplaceListener? = null
    ): Boolean {
        if (src == null) return false
        return if (src.isDirectory) {
            copyDir(src, dest, listener)
        } else copyFile(
            src,
            dest,
            listener
        )
    }

    /**
     * Copy the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyDir(
        srcDir: File,
        destDir: File?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyFile(
        srcFile: File,
        destFile: File?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * 移动文件或目录
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(
        srcPath: String?,
        destPath: String?
    ): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * Move the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(
        srcPath: String?,
        destPath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }
    /**
     * Move the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    /**
     * Move the directory or file.
     *
     * @param src  The source.
     * @param dest The destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(
        src: File?,
        dest: File?,
        listener: OnReplaceListener? = null
    ): Boolean {
        if (src == null) return false
        return if (src.isDirectory) {
            moveDir(src, dest, listener)
        } else moveFile(
            src,
            dest,
            listener
        )
    }

    /**
     * 移动目录
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * 移动文件
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null) return false
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
                } else if (file.isDirectory) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(
                    srcFile,
                    destFile
                )
            ) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createOrExistsDir(destFile.parentFile)) false else try {
            (UtilsBridge.writeFileFromIS(destFile.absolutePath, FileInputStream(srcFile))
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除文件or目录
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(filePath: String?): Boolean {
        return delete(getFileByPath(filePath))
    }

    /**
     * Delete the directory.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    /**
     * 删除目录
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除文件
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 清空目标牡蛎
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dirPath: String?): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * Delete the all in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { true }
    }

    /**
     * 清空目标目录下的文件
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * Delete all files in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?
    ): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter?): Boolean {
        if (dir == null || filter == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }
    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath    The path of directory.
     * @param comparator The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String?, comparator: Comparator<File?>? = null): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), false, comparator)
    }
    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir        The directory.
     * @param comparator The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?, comparator: Comparator<File?>? = null): List<File?> {
        return listFilesInDir(dir, false, comparator)
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String?, isRecursive: Boolean): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(
        dirPath: String?,
        isRecursive: Boolean,
        comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDir(getFileByPath(dirPath), isRecursive, comparator)
    }
    /**
     * Return the files in directory.
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(
        dir: File?,
        isRecursive: Boolean,
        comparator: Comparator<File?>? = null
    ): List<File?> {
        return listFilesInDirWithFilter(dir, { true }, isRecursive, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath    The path of directory.
     * @param filter     The filter.
     * @param comparator The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter,
        comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir        The directory.
     * @param filter     The filter.
     * @param comparator The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter,
        comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(dir, filter, false, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter,
        isRecursive: Boolean,
        comparator: Comparator<File?>?
    ): List<File?> {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive, comparator)
    }
    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter,
        isRecursive: Boolean = false,
        comparator: Comparator<File?>? = null
    ): List<File?> {
        val files = listFilesInDirWithFilterInner(dir, filter, isRecursive)
        if (comparator != null) {
            Collections.sort(files, comparator)
        }
        return files
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    private fun listFilesInDirWithFilterInner(
        dir: File?,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File?> {
        val list: MutableList<File?> = ArrayList()
        if (!isDir(dir)) return list
        val files = dir!!.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true))
                }
            }
        }
        return list
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(filePath: String?): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * Return the charset of file simply.
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(filePath: String?): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * Return the charset of file simply.
     *
     * @param file The file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(file: File?): String {
        if (file == null) return ""
        if (isUtf8(file)) return "UTF-8"
        var p = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            p = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return when (p) {
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(filePath: String?): Boolean {
        return isUtf8(getFileByPath(filePath))
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(file: File?): Boolean {
        if (file == null) return false
        var `is`: InputStream? = null
        try {
            val bytes = ByteArray(24)
            `is` = BufferedInputStream(FileInputStream(file))
            val read = `is`.read(bytes)
            return if (read != -1) {
                val readArr = ByteArray(read)
                System.arraycopy(bytes, 0, readArr, 0, read)
                isUtf8(readArr) == 100
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private fun isUtf8(raw: ByteArray): Int {
        var utf8 = 0
        var ascii = 0
        if (raw.size > 3) {
            if (raw[0] == 0xEF.toByte() && raw[1] == 0xBB.toByte() && raw[2] == 0xBF.toByte()) {
                return 100
            }
        }
        val len: Int = raw.size
        var child = 0
        var i = 0
        while (i < len) {
            // UTF-8 byte shouldn't be FF and FE
            if (raw[i] and 0xFF.toByte() == 0xFF.toByte() || raw[i] and 0xFE.toByte() == 0xFE.toByte()) {
                return 0
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if (raw[i] and 0x7F.toByte() == raw[i] && raw[i] != 0.toByte()) {
                    ascii++
                } else if (raw[i] and 0xC0.toByte() == 0xC0.toByte()) {
                    // 0x11****** maybe is UTF-8
                    for (bit in 0..7) {
                        child = if ((0x80 shr bit).toByte() and raw[i] == (0x80 shr bit).toByte()) {
                            bit
                        } else {
                            break
                        }
                    }
                    utf8++
                }
                i++
            } else {
                child = if (raw.size - i > child) child else raw.size - i
                var currentNotUtf8 = false
                for (children in 0 until child) {
                    // format must is 0x10******
                    if (raw[i + children] and 0x80.toByte() != 0x80.toByte()) {
                        if (raw[i + children] and 0x7F.toByte() == raw[i + children] && raw[i].toInt() != 0) {
                            // ASCII format is 0x0*******
                            ascii++
                        }
                        currentNotUtf8 = true
                    }
                }
                if (currentNotUtf8) {
                    utf8--
                    i++
                } else {
                    utf8 += child
                    i += child
                }
                child = 0
            }
        }
        // UTF-8 contains ASCII
        return if (ascii == len) {
            100
        } else (100 * ((utf8 + ascii).toFloat() / len.toFloat())).toInt()
    }

    /**
     * Return the number of lines of file.
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    fun getFileLines(filePath: String?): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * Return the number of lines of file.
     *
     * @param file The file.
     * @return the number of lines of file
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP.endsWith("\n")) {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i].toInt().toChar() == '\n') ++count
                    }
                }
            } else {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i].toInt().toChar() == '\r') ++count
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return count
    }

    /**
     * Return the size.
     *
     * @param filePath The path of file.
     * @return the size
     */
    fun getSize(filePath: String?): String {
        return getSize(getFileByPath(filePath))
    }

    /**
     * Return the size.
     *
     * @param file The directory.
     * @return the size
     */
    fun getSize(file: File?): String {
        if (file == null) return ""
        return if (file.isDirectory) {
            getDirSize(file)
        } else getFileSize(file)
    }

    /**
     * Return the size of directory.
     *
     * @param dir The directory.
     * @return the size of directory
     */
    private fun getDirSize(dir: File): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else UtilsBridge.byte2FitMemorySize(len)
    }

    /**
     * Return the size of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileSize(file: File): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else UtilsBridge.byte2FitMemorySize(len)
    }

    /**
     * Return the length.
     *
     * @param filePath The path of file.
     * @return the length
     */
    fun getLength(filePath: String?): Long {
        return getLength(getFileByPath(filePath))
    }

    /**
     * Return the length.
     *
     * @param file The file.
     * @return the length
     */
    fun getLength(file: File?): Long {
        if (file == null) return 0
        return if (file.isDirectory) {
            getDirLength(file)
        } else getFileLength(file)
    }

    /**
     * Return the length of directory.
     *
     * @param dir The directory.
     * @return the length of directory
     */
    private fun getDirLength(dir: File): Long {
        if (!isDir(dir)) return 0
        var len: Long = 0
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                len += if (file.isDirectory) {
                    getDirLength(file)
                } else {
                    file.length()
                }
            }
        }
        return len
    }

    /**
     * 获取文件体积
     *
     * @param filePath 文件路径，既可以是本地文件，也可以是网络url
     * @return the length of file
     */
    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches(Regex("[a-zA-z]+://[^\\s]*"))
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpsURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileLength(file: File?): Long {
        return if (!isFile(file)) -1 else file!!.length()
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(filePath: String?): String {
        val file = if (UtilsBridge.isSpace(filePath)) null else File(filePath)
        return getFileMD5ToString(file)
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(file: File?): String {
        return ConvertUtils.bytes2HexString(getFileMD5(file))
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5(filePath: String?): ByteArray? {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Return the file's path of directory.
     *
     * @param file The file.
     * @return the file's path of directory
     */
    fun getDirName(file: File?): String {
        return if (file == null) "" else getDirName(file.absolutePath)
    }

    /**
     * Return the file's path of directory.
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    fun getDirName(filePath: String): String {
        if (UtilsBridge.isSpace(filePath)) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }


    /**
     * 获取文件名不包含扩展名
     *
     * @param file The file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(file: File?): String {
        return file?.nameWithoutExtension ?: ""
    }

    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(filePath: String): String {
        if (UtilsBridge.isSpace(filePath)) return ""
       return getFileNameNoExtension(getFileByPath(filePath))
    }

    /**
     * Return the extension of file.
     *
     * @param file The file.
     * @return the extension of file
     */
    @JvmStatic
    fun getFileExtension(file: File?): String {
        return file?.extension ?: ""
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    @JvmStatic
    fun getFileExtension(filePath: String): String {
        if (UtilsBridge.isSpace(filePath)) return ""
        return getFileExtension(getFileByPath(filePath))
    }

    /**
     * Notify system to scan the file.
     *
     * @param filePath The path of file.
     */
    @JvmStatic
    fun notifySystemToScan(filePath: String?) {
        notifySystemToScan(getFileByPath(filePath))
    }

    /**
     * Notify system to scan the file.
     *
     * @param file The file.
     */
    @JvmStatic
    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) return
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.parse("file://" + file.absolutePath)
        getApp().sendBroadcast(intent)
    }

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    @JvmStatic
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val totalSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            totalSize = statFs.blockCountLong
        } else {
            blockSize = statFs.blockSize.toLong()
            totalSize = statFs.blockCount.toLong()
        }
        return blockSize * totalSize
    }

    /**
     * Return the available size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the available size of file system
     */
    @JvmStatic
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val availableSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            availableSize = statFs.availableBlocksLong
        } else {
            blockSize = statFs.blockSize.toLong()
            availableSize = statFs.availableBlocks.toLong()
        }
        return blockSize * availableSize
    }


}