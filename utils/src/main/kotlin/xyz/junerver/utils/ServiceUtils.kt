package xyz.junerver.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import java.lang.Exception

/**
 * @Author Junerver
 * @Date 2021/11/5-14:08
 * @Email junerver@gmail.com
 * @Version v1.0
 * @Description
 */
object ServiceUtils {



    fun isServiceRunning(serviceName: String): Boolean{
        return isServiceRunning(Utils.getApp().applicationContext,serviceName)
    }

    /**
     * 判断Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service 类全名
     * @return true 表示正在运行，false 表示没有运行
     */
    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceInfoList = manager.getRunningServices(200)
        if (serviceInfoList.size <= 0) {
            return false
        }
        for (info in serviceInfoList) {
            if (info.service.className == serviceName) {
                return true
            }
        }
        return false
    }

    /**
     * Start the service.
     *
     * @param intent The intent.
     */
    fun startService(intent: Intent) {
        try {
            intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.getApp().startForegroundService(intent)
            } else {
                Utils.getApp().startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Start the service.
     *
     * @param className The name of class.
     */
    fun startService(@NonNull className: String?) {
        try {
            startService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Start the service.
     *
     * @param cls The service class.
     */
    fun startService(@NonNull cls: Class<*>?) {
       startService(Intent(Utils.getApp(), cls))
    }

    /**
     * Stop the service.
     *
     * @param className The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(@NonNull className: String?): Boolean {
        return try {
            stopService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Stop the service.
     *
     * @param cls The name of class.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(@NonNull cls: Class<*>?): Boolean {
        return stopService(Intent(Utils.getApp(), cls))
    }

    /**
     * Stop the service.
     *
     * @param intent The intent.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(@NonNull intent: Intent?): Boolean {
        return try {
            Utils.getApp().stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}