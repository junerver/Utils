package com.junerver.utils

import android.app.Application
import java.io.File
import android.annotation.SuppressLint
import com.junerver.utils.constant.MemoryConstants
import java.lang.IllegalArgumentException


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