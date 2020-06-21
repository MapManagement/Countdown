package com.example.countdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declaring variables that contain TextViews
        val yearsText: TextView = findViewById(R.id.yearsText)
        val daysText: TextView = findViewById(R.id.daysText)
        val hoursText: TextView = findViewById(R.id.hoursText)
        val minutesText: TextView = findViewById(R.id.minutesText)
        val secondsText: TextView = findViewById(R.id.secondsText)

        object : CountDownTimer(10000, 1000){
            override fun onFinish() {
                secondsText.text = "0 Seconds"
            }

            override fun onTick(p0: Long) {
                val time = p0 / 1000
                secondsText.text = "$time Second"
            }
        }.start()
    }

    private fun setTimeTexts() {


    }
}
