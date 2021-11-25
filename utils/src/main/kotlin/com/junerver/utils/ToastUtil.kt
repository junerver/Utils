package com.junerver.utils

import android.widget.Toast
import androidx.annotation.StringRes

/**
 * @Author Junerver
 * @Date 2019/5/29-14:23
 * @Email junerver@gmail.com
 * @Version v1.0
 * @Description 统一处理吐司提示
 */
object ToastUtil {

    private val mToast by lazy { init() }

    private fun init(): Toast = Toast.makeText(Utils.getApp(), "", Toast.LENGTH_LONG)

    fun showShortToast(msg: String) {
        mToast.setText(msg)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.show()
    }

    fun showShortToast(@StringRes msg:Int) {
        mToast.setText(msg)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.show()
    }

    fun showLongToast(msg: String) {
        mToast.setText(msg)
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }

    fun showLongToast(@StringRes msg:Int) {
        mToast.setText(msg)
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }
}