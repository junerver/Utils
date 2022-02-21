package xyz.junerver.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import com.junerver.utils.R
import kotlinx.android.synthetic.main.activity_main.*
import xyz.junerver.utils.ex.*
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var mLastFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tvTestDsl.buildSpanString {
            addText("这是第一个字符串"){
                setColor("#009966")
            }
            addText("他有很多组成部分")
            addText("颜色"){
                setColor("#0099FF")
            }
            addText("可点击"){
                onClick {
                    Log.d(TAG, "这是dsl中的点击事件")
                }
            }
        }

        Log.d(TAG, "metadata: ${getMetaData("value_key","def")}")
        btnEnable.setOnClickListener {

//            PermissionUtils.permission(Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO)
//
//                .callback { isAllGranted, granted, deniedForever, denied ->
//                    Log.d(TAG, "onCreate: \n$isAllGranted \n$granted \n$deniedForever \n$denied")
//                }
//                .request()

//            invisibles(btnClick,btnToast)

            Log.d(TAG, "onCreate: ${etString.trimTextStr}")

        }

        //无效
        etString.keyListener = object :DigitsKeyListener(Locale.CHINA){
            override fun getInputType(): Int {
                return InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            }

            override fun getAcceptedChars(): CharArray {
                return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
            }
        }

        etString.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })

        etString.addTextChangedListener {
            afterTextChanged {
                if (it.toString().length >= 4) {
                    KeyboardUtils.toggleSoftInput()
                }
            }
        }

        etString.addTextChangedListenerClosure(
            afterTextChanged = {
                if (it.toString().length >= 4) {
                    KeyboardUtils.toggleSoftInput()
                }
            },
        )

        etString.addTextChangedListenerClosure { charSequence, i, i2, i3 ->
            println("on call")
        }

        btnClick.setOnClickListener {
            KeyboardUtils.toggleSoftInput()
        }

    }
}