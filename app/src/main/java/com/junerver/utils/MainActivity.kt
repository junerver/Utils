package com.junerver.utils

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnEnable.setOnClickListener {

            PermissionUtils.permission(Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO)

                .callback { isAllGranted, granted, deniedForever, denied ->
                    Log.d(TAG, "onCreate: \n$isAllGranted \n$granted \n$deniedForever \n$denied")
                }
                .request()

        }
    }
}