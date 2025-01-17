package xyz.junerver.utils

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.DynamicDrawableSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.junerver.utils.R
import xyz.junerver.utils.ex.*
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var mLastFile = ""
    lateinit var activity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        Log.d(TAG, "onCreate: 我是生命周期")
        findViewById<TextView>(R.id.tvTestDsl).buildSpannableString {
            "这是另一个测试".asSpannableString {
                setBackgroundColor("#ff0099")
                onClick(false) {

                }
            }
            "你甚至还可以这样用"{
                setBackgroundColor("#ff0099")
                onClick(false) {
                }
            }
            addText("我已详细阅读并同意") {
                setBackgroundColor("#FF9900")
                setStyle(Typeface.BOLD_ITALIC)
                userUnderLine()
                useStrikethrough()
                setDrawableRight(R.drawable.icon_dingding, DynamicDrawableSpan.ALIGN_CENTER)
            }
            addText("打电话") {
                asURL("tel:10086")
            }
            addText("《隐私政策》") {
                setForegroundColor("#0099FF")
                setDrawableLeft(R.drawable.icon_wechat)
                onClick() {
                    requestedOrientation =
                        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            //横屏 0  竖屏1
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            //未定义
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                }
            }
        }

        val jo = json {
            "name" to "zhangsan"
            val arr = arrayOf(1,2,3,4)
            "keys" to arr.map {
                json {
                    "key" to it
                    "index" to "学号：$it"
                }
            }.toTypedArray()
            "info" to json {
                "email" to "xxxx@mail.com"
                "age" to 18
                "sex" to '1'
            }
            "o" to {
                "2" to "3"
                "arr" to listOf<String>("1","2","3")
            }
        }

        findViewById<TextView>(R.id.tvContent).text = jo.toString()

        Log.d(TAG, "metadata: ${getMetaData("value_key", "def")}")
        val btnEnable = findViewById<Button>(R.id.btnEnable)
        val btnClick = findViewById<Button>(R.id.btnClick)
        val btnToast = findViewById<Button>(R.id.btnToast)
        val etString = findViewById<EditText>(R.id.etString)
        btnEnable.setOnClickListener {

            PermissionUtils.permission(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO
            )

                .callback { isAllGranted, granted, deniedForever, denied ->
                    Log.d(TAG, "onCreate: \n$isAllGranted \n$granted \n$deniedForever \n$denied")
                }
                .request()

            invisibles(btnClick, btnToast)

            Log.d(TAG, "onCreate: ${etString.trimTextStr}")

        }

        //无效
//        etString.keyListener = object : DigitsKeyListener(Locale.CHINA) {
//            override fun getInputType(): Int {
//                return InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
//            }
//
//            override fun getAcceptedChars(): CharArray {
//                return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
//            }
//        }

        etString.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        etString.addTextChangedListenerDsl {
            afterTextChanged {
                if (it.toString().length >= 4) {
                    KeyboardUtils.toggleSoftInput()
                }
            }
        }

        etString.addTextChangedListener(
            afterTextChanged = {
                if (it.toString().length >= 4) {
                    KeyboardUtils.toggleSoftInput()
                }
            }
        )
        etString.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->

        }


        btnClick.setOnClickListener {
            KeyboardUtils.toggleSoftInput()
            //切换横竖屏
            requestedOrientation =
                if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
        }

//        btnClick.setSingleClickListener{
//            Log.d(TAG, "onCreate: 点击成功")
//        }
//
//        btnClick2.setSingleClickListener{
//            Log.d(TAG, "onCreate2: 点击成功")
//        }
    }
}