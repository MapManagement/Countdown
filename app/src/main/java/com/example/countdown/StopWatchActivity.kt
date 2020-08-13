package com.example.countdown

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class StopWatchActivity : AppCompatActivity() {

    var currentColor: String? = "#e01c18"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

