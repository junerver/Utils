package com.junerver.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import java.util.*
import kotlin.system.exitProcess

/**
* Description: Act 管理工具，使用时在APP中调用 [register] 函数即可
* @author Junerver
* @date: 2021/11/23-11:15
* @Email: junerver@gmail.com
* @Version: v1.0
*/
object ActivityManager {

    //注册activity生命周期回调函数
    fun register(app: Application) {
        app.registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                add(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                remove(activity)
            }

        })
    }

    /** 保存 Activity 对象的堆栈 */
    private val activityStack: Stack<Activity> = Stack()

    /**
     * 添加 Activity 到堆栈
     *
     * @param activity Activity 对象
     */
    private fun add(activity: Activity) {
        activityStack.add(activity)
        Log.d("AppManager---->>", "add---->>$activity size---->>${activityStack.size}")
    }

    /**
     * 将 Activity 从堆栈移除
     *
     * @param activity Activity 对象
     */
    private fun remove(activity: Activity) {
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
            Log.d("AppManager---->>", "remove---->>$activity size---->>${activityStack.size}")
        }
    }

    /**
     * 结束指定 Activity
     *
     * @param activity Activity 对象
     */
    fun finish(activity: Activity) {
        if (activityStack.contains(activity)) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }

    /**
     * 结束指定 Activity
     *
     * @param cls Activity 类对象
     */
    fun finish(clazz: Class<out Activity>) {
        val del: Activity? = activityStack.lastOrNull { it.javaClass == clazz }
        del?.let {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }

    /**
     * 获取栈顶的 Activity
     *
     * @return 栈顶的 Activity 对象
     */
    fun peek(): Activity {
        return activityStack.peek()
    }

    /**
     * 根据类，获取 Activity 对象
     *
     * @param clazz Activity 类
     * @param <T> Activity 类型
     *
     * @return Activity对象
     */
    fun <A : Activity> get(clazz: Class<out Activity>): A? {
        var target: A? = null
        activityStack
                .filter { it.javaClass == clazz }
                .forEach {
                    @Suppress("UNCHECKED_CAST")
                    target = it as A
                }
        return target
    }

    /**
     * 结束所有Activity
     */
    fun finishAll() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
        Log.d("AppManager---->>", "Finish All Activity!")
    }

    /**
     * 结束所有Activity, 除了指定的activity不移除
     */
    fun finishAllExclude(clazz: Class<*>) {
        Log.d("AppManager---->>","调用了关闭业务，排除${clazz.name}" )
        activityStack
            .filter { it.javaClass != clazz }
            .forEach {
                finish(it)
            }
    }

    //递归关闭所有位于指定activity之上的activity
    fun finishActivityAfter(clazz: Class<*>): Boolean {
        val last = peek()
        if (last.javaClass != clazz) {
            finish(last)
            Log.d("AppManager---->>","+++++++执行关闭：$last")
            finishActivityAfter(clazz)
        } else {
            return true
        }
        return false
    }


    /**
     * 退出应用程序
     */
    @SuppressLint("MissingPermission")
    fun appExit() {
        try {
            finishAll()
            val activityMgr = peek().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(peek().packageName)
            exitProcess(0)
        } catch (e: Exception) {
            Log.d("AppManager---->>", "Application Exit!")
        }
    }
}