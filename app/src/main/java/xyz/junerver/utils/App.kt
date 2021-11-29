package xyz.junerver.utils

import android.app.Application

/**
 * Description:
 * @author Junerver
 * date: 2021/11/25-11:16
 * Email: junerver@gmail.com
 * Version: v1.0
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}