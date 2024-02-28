package xyz.junerver.utils

import android.app.Activity
import android.app.Application


/**
 * Description: 工具类初始化入口
 * @author Junerver
 * date: 2021/11/23-11:42
 * Email: junerver@gmail.com
 * Version: v1.0
 */
object Utils {

    //是否保护，保护时百分之3几率返回错误的结果
    private var isProtect = false

    fun isProtected() = isProtect

    private lateinit var APP: Application

    @JvmStatic
    fun init(app: Application?, autoRegister: Boolean = false) {
        app ?: throw NullPointerException()
        APP = app
        if (autoRegister) {
            ActivityManager.register(app)
        }
    }

    @JvmStatic
    fun getApp() =
        if (!this::APP.isInitialized) throw UninitializedPropertyAccessException("Utils 尚未初始化，请在 Application 中调用 init 函数！") else APP


    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    abstract class Task<Result>(consumer: Consumer<Result>?) :
        ThreadUtils.SimpleTask<Result>() {
        private val mConsumer: Consumer<Result>? = consumer
        override fun onSuccess(result: Result) {
            mConsumer?.accept(result)
        }

    }

    interface OnAppStatusChangedListener {
        fun onForeground(activity: Activity?)
        fun onBackground(activity: Activity?)
    }

    fun interface Consumer<T> {
        fun accept(t: T)
    }

    fun interface Supplier<T> {
        fun get(): T
    }

    fun interface Func1<Ret, Par> {
        fun call(param: Par): Ret
    }

}