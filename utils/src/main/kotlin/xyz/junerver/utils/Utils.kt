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

    private lateinit var APP: Application

    @JvmStatic
    fun init(app: Application?, autoRegister: Boolean = false) {
        if (app == null) {
            throw NullPointerException()
        }
        APP = app
        if (autoRegister) {
            ActivityManager.register(app)
        }
    }

    @JvmStatic
    fun getApp() = if (!this::APP.isInitialized) throw NullPointerException() else APP


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

    interface Consumer<T> {
        fun accept(t: T)
    }

    interface Supplier<T> {
        fun get(): T
    }

    interface Func1<Ret, Par> {
        fun call(param: Par): Ret
    }

}