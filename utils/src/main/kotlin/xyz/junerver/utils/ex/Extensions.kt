package xyz.junerver.utils.ex


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Size
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import xyz.junerver.utils.ByteUtils.toBase64
import xyz.junerver.utils.groupStringByLength
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


//region convert扩展
/**
 * dp转px
 */
fun View.dp2px(dipValue: Float): Float {
    return (dipValue * this.resources.displayMetrics.density + 0.5f)
}

/**
 * px转dp
 */
fun View.px2dp(pxValue: Float): Float {
    return (pxValue / this.resources.displayMetrics.density + 0.5f)
}

/**
 * sp转px
 */
fun View.sp2px(spValue: Float): Float {
    return (spValue * this.resources.displayMetrics.scaledDensity + 0.5f)
}
//endregion

//region Activity扩展
/**
 * @Description 隐藏Act的底部虚拟键盘
 * @Author Junerver
 * Created at 2019-06-26 11:04
 * @param
 * @return
 */
fun Activity.hideBottomUIMenu() {
    //隐藏虚拟按键，并且全屏
    if (Build.VERSION.SDK_INT in 12..18) { // lower api
        val v = this.window.decorView
        v.systemUiVisibility = View.GONE
    } else if (Build.VERSION.SDK_INT >= 19) {
        //for new api versions.
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }
}
//endregion

const val TAG = "Extensions"


//region view扩展
inline fun View.setSingleClickListener(crossinline block: (View) -> Unit) {
    var lastTime = 0L
    setOnClickListener {
        try {
            if (System.currentTimeMillis() - lastTime < 1500) return@setOnClickListener
            block(it)
        } finally {
            lastTime = System.currentTimeMillis()
        }
    }
}

fun View.gone() {
    this.visibility = View.GONE
}

fun gones(vararg views: View) {
    views.forEach {
        it.gone()
    }
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun invisibles(vararg views: View) {
    views.forEach {
        it.invisible()
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun visibles(vararg views: View) {
    views.forEach {
        it.visible()
    }
}
//endregion


//region String 扩展
/**
 * Description: 校验是否为合法文件名
 * @author Junerver
 * @date: 2021/12/30-14:31
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param
 * @return
 */
fun String.isValidFileName(): Boolean {
    val regex =
        "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"
    return Pattern.matches(regex, this)
}

/**
 * 验证是否手机
 */
fun String.isMobile(): Boolean {
    val regex = "(\\+\\d+)?1[34578]\\d{9}$"
    return Pattern.matches(regex, this)
}

/**
 * 验证是否电话
 */
fun String.isPhone(): Boolean {
    val regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$"
    return Pattern.matches(regex, this)

}

/**
 * 验证是否邮箱
 */
fun String.isEmail(): Boolean {
    val emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
    val matcher = emailPattern.matcher(this)
    if (matcher.find()) {
        return true
    }
    return false
}

/**
 * 字符串安全转换到整型，转换失败返回0
 */
fun String.safeConvertToInt(): Int {
    return try {
        toInt()
    } catch (e: Exception) {
        0
    }
}

/**
 * 字符串安全转换到长整型，转换失败返回0
 */
fun String.safeConvertToLong(): Long {
    return try {
        toLong()
    } catch (e: Exception) {
        0L
    }
}

/**
 * 字符串安全转换到双精度类型，转换失败返回0
 */
fun String.safeConvertToDouble(): Double {
    return try {
        toDouble()
    } catch (e: Exception) {
        0.0
    }
}

/**
 * 字符串安全转换到短整型类型，转换失败返回0
 */
fun String.safeConvertToShort(): Short {
    return try {
        toShort()
    } catch (e: Exception) {
        0
    }
}

//字符串直接转换为Base64
fun String.toBase64(): String = this.toByteArray().toBase64()

//base64字符串 转码为BA
fun String.base64toByteArray(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

//base64字符串直接解码为普通字符串
fun String.decodeBase64(): String = String(this.base64toByteArray())

//字符串按长度分组
fun String.groupByLength(length: Int): List<String> = groupStringByLength(this, length)

//首字母大写
fun String.upperFirstLetter(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        if (!Character.isLowerCase(this[0])) return this
        return (this[0].code - 32).toChar() + this.substring(1)
    }

}

fun String.urlEncode(): String {
    val encode: String = URLEncoder.encode(this, "utf-8")
    return encode.replace("%3A", ":").replace("%2F", "/")
}

/**
 * @作者 尧
 * @功能 String左对齐
 */
fun String.padLeft(len: Int, ch: Char): String {
    val diff = len - this.length
    if (diff <= 0) {
        return this
    }
    val charr = CharArray(len)
    System.arraycopy(this.toCharArray(), 0, charr, 0, this.length)
    for (i in this.length until len) {
        charr[i] = ch
    }
    return String(charr)
}


/**
 * @作者 尧
 * @功能 String右对齐 向左侧补充字符
 */
fun String.padRight(len: Int, ch: Char): String {
    val diff = len - this.length
    if (diff <= 0) {
        return this
    }
    val charr = CharArray(len)
    System.arraycopy(this.toCharArray(), 0, charr, diff, this.length)
    for (i in 0 until diff) {
        charr[i] = ch
    }
    return String(charr)
}

@ExperimentalContracts
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && !this.trim().equals("null", true) && this.trim().isNotEmpty()
}

//endregion

//region Json & Gson
/** json相关 **/
fun Any.toJson(
    dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    lenient: Boolean = false,
    excludeFields: List<String>? = null
) = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if (lenient) setLenient()
        if (!excludeFields.isNullOrEmpty()) {
            setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f != null && excludeFields.contains(f.name)
                }

                override fun shouldSkipClass(clazz: Class<*>?) = false
            })
        }
    }
    .create().toJson(this)!!

inline fun <reified T> String.toBean(
    dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    lenient: Boolean = false
) = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if (lenient) setLenient()
    }.create()
    .fromJson<T>(this, object : TypeToken<T>() {}.type)!!
//endregion

//region context扩展
fun Context.getDrawableRes(@DrawableRes id: Int): Drawable {
    return AppCompatResources.getDrawable(this, id)!!
}

fun Context.getColorRes(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.inflater(resource: Int): View {
    return LayoutInflater.from(this).inflate(resource, null)
}


fun Context.inflater(resource: Int, root: ViewGroup, attachToRoot: Boolean): View {
    return LayoutInflater.from(this).inflate(resource, root, attachToRoot)
}

/**
 * 获取屏幕宽度
 *
 * @return 屏幕宽度
 */
fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * 获取屏幕高度
 *
 * @return 屏幕高度
 */
fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 获取状态栏高度
 *
 * @return 状态栏高度（px）
 */
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    var statusBarHeight = 0
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }

    return statusBarHeight
}

/**
 * 获取Version code
 *
 * @return version code
 */
fun Context.versionCode(): Int {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } else {
        packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
    }
}

/**
 * 获取Version name
 *
 * @return version name
 */
fun Context.versionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

/**
 * 获取像素密集度参数density
 *
 * @return density
 */
fun Context.density(): Float {
    return resources.displayMetrics.density
}

/**
 * 检查设备是否有虚拟键盘
 */
fun Context.checkDeviceHasNavigationBar(): Boolean {
    var hasNavigationBar = false
    val rs = this.resources
    val id = rs
        .getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id)
    }
    try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val m = systemPropertiesClass.getMethod("get", String::class.java)
        val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
        if ("1" == navBarOverride) {
            hasNavigationBar = false
        } else if ("0" == navBarOverride) {
            hasNavigationBar = true
        }
    } catch (e: Exception) {
        Log.e("ContextExtend", "检查虚拟键盘：")
    }
    return hasNavigationBar
}

/**
 * @Description 适配安卓7.0中Uri，需要注意在AndroidManifest文件中注册——provider
 * @Author Junerver
 * Created at 2018/12/22 09:35
 * @param file 需要获取 uri 的 file
 * @return
 */
fun Context.getUriForFile(file: File): Uri {
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            this.applicationContext,
            "${this.packageName}.FileProvider",
            file
        )
    } else {
        Uri.fromFile(file)
    }
}

//fun Context.showDialog(content: String, contentColor: Int = Color.parseColor("#333333"), positiveColor: Int = Color.parseColor("#3F51B5")) {
//    MaterialDialog.Builder(this)
//            .content(content)
//            .contentColor(contentColor)
//            .positiveText("确定")
//            .positiveColor(positiveColor)
//            .show()
//}

//fun Context.showDialog(str: String, r: () -> Unit) {
//    MaterialDialog.Builder(this)
//            .content(str)
//            .contentColorRes(R.color.dialog)
//            .positiveText("确定")
//            .onPositive { _, _ -> kotlin.run(r) }
//            .positiveColorRes(R.color.dialogPrimary)
//            .negativeText("取消")
//            .negativeColorRes(R.color.dialogPrimary)
//            .show()
//}


/**
 * Description: 获取manifest文件中的metadata对象数据
 * @author Junerver
 * @date: 2021/2/8-10:17
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param key  meta-data 中的android:name
 * @param def 当没有取到该字段值时的缺省值
 * @return
 */
inline fun <reified T> Context.getMetaData(key: String, def: T): T {
    val applicationInfo =
        this.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val data = applicationInfo.metaData.get(key)
    return data?.let {
        when (T::class) {
            Int::class, String::class, Float::class, Boolean::class -> it
            Long::class -> (data as Float).toLong() //存储时以float类型保存
            else -> throw IllegalArgumentException("META-DATA 类型错误")
        } as T
    } ?: kotlin.run { def }
}

/**
 * Description: 拷贝Assets目录下的文件
 * @author Junerver
 * @date: 2021/11/23-11:32
 * @Email: junerver@gmail.com
 * @Version: v1.0
 * @param assetName assets目录下对应的文件名
 * @param savePath 目标目录
 * @param saveName 目标文件名称
 * @throws IOException
 */
@Throws(IOException::class)
fun Context.copyAssetFile(assetName: String, savePath: String, saveName: String) {
    val dir = File(savePath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val dbf = File(savePath + saveName)
    if (dbf.exists()) {
        dbf.delete()
    }
    val outFileName = savePath + saveName
    val myOutput = FileOutputStream(outFileName)
    val myInput = this.assets.open(assetName)
    val buffer = ByteArray(1024)
    var length: Int
    while (myInput.read(buffer).also { length = it } > 0) {
        myOutput.write(buffer, 0, length)
    }
    myOutput.flush()
    myInput.close()
    myOutput.close()
}
//endregion

//region 全局的UI线程回调函数
fun <T> T.postUI(action: () -> Unit) {
    // Fragment
    if (this is Fragment) {
        val fragment = this
        if (!fragment.isAdded) return
        val activity = fragment.activity ?: return
        if (activity.isFinishing) return
        activity.runOnUiThread(action)
        return
    }
    // Activity
    if (this is Activity) {
        if (this.isFinishing) return

        this.runOnUiThread(action)
        return
    }
    // 主线程
    if (Looper.getMainLooper() === Looper.myLooper()) {
        action()
        return
    }
    // 子线程，使用handler
    KitUtil.handler.post { action() }
}

object KitUtil {
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
}
//endregion

//region 带参数的单例封装
/**
 * 使用方法：
 * class WorkSingleton private constructor(context: Context) {
 *     init {
 *         // Init using context argument
 *     }
 *
 *     companion object : SingletonHolder<WorkSingleton, Context>(::WorkSingleton)
 * }
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
//endregion