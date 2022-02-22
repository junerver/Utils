package xyz.junerver.utils

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.style.DynamicDrawableSpan
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


        tvTestDsl.buildSpannableString {
            addText("我已详细阅读并同意"){
                setBackgroundColor("#FF9900")
                setStyle(Typeface.BOLD_ITALIC)
                userUnderLine()
                useStrikethrough()
                setDrawableRight(R.drawable.icon_dingding,DynamicDrawableSpan.ALIGN_CENTER)
            }
            addText("打电话"){
                asURL("tel:10086")
            }
            addText("《隐私政策》"){
                setForegroundColor("#0099FF")
                setDrawableLeft(R.drawable.icon_wechat)
                onClick(false) {
                    Log.d(TAG, "点击了隐私政策！")
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