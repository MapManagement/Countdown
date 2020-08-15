package com.example.countdown

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class StopWatchActivity : AppCompatActivity() {

    var currentSeconds: Int = 0
    var currentColor: String? = "#e01c18"

    var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        var startedAt = sharedPref.getString("startedAt", "")
        val running = sharedPref.getBoolean("running", false)
        isRunning = running


        val openFAB: FloatingActionButton = findViewById(R.id.floating_point)
        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)

        startFAB.setOnClickListener {
            if (running) {
                val timePeriodinSeconds = getTimePeriod(startedAt)
                startTimer(convertSeconds(timePeriodinSeconds), startedAt)
            }
            else {
                val newStartedAt = setStartedAtDateTime()
                val timePeriodinSeconds = getTimePeriod(newStartedAt)
                startTimer(convertSeconds(timePeriodinSeconds), newStartedAt)
            }
        }
        resetFAB.setOnClickListener {}
        openFAB.setOnClickListener {}
        colorFAB.setOnClickListener {}
    }

    private fun startTimer(timePeriod: ArrayList<Int>, chosenDateTime: String?) {
        setTexts(timePeriod)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }

        val handler = Handler()
        handler.post(Runnable {
            getTimePeriod()
        })

    }

    private fun setTexts(timePeriodArray: ArrayList<Int>) {
        val totalSeconds = timePeriodArray[5]
        if (totalSeconds < 31536000) {
            yearsText.setTextColor(Color.parseColor(currentColor))
            if (totalSeconds < 86400) {
                daysText.setTextColor(Color.parseColor(currentColor))
                if (totalSeconds < 3600) {
                    hoursText.setTextColor(Color.parseColor(currentColor))
                    if (totalSeconds < 60) {
                        minutesText.setTextColor(Color.parseColor(currentColor))
                        if (totalSeconds < 1) {
                            secondsText.setTextColor(Color.parseColor(currentColor))
                        }
                    }
                }
            }
        }

        yearsText.text = timePeriodArray[0].toString() + " YEARS"
        daysText.text = timePeriodArray[1].toString() + " DAYS"
        hoursText.text = timePeriodArray[2].toString() + " HOURS"
        minutesText.text = timePeriodArray[3].toString() + " MINUTES"
        secondsText.text = timePeriodArray[4].toString() + " SECONDS"
    }

    private fun colorTextsWhite() {
        yearsText.setTextColor(Color.parseColor("#ffffff"))
        daysText.setTextColor(Color.parseColor("#ffffff"))
        hoursText.setTextColor(Color.parseColor("#ffffff"))
        minutesText.setTextColor(Color.parseColor("#ffffff"))
        secondsText.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun getTimePeriod(startedAt: String?): Long {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val currentDateTime = Calendar.getInstance().time

        val formattedCurrentDateTime = date.format(currentDateTime)

        fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

        val startedAtSeconds: Long = LocalDateTime.parse(startedAt).toMillis() / 1000
        val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

        return currentSeconds - startedAtSeconds
    }

    private fun convertSeconds(totalSeconds: Long): ArrayList<Int> {
        val years = (totalSeconds / 31536000).toInt()
        val days = ((totalSeconds % 31536000) / 86400).toInt()
        val hours = (((totalSeconds % 31536000) % 86400) / 3600).toInt()
        val minutes = ((((totalSeconds % 31536000) % 86400) % 3600) / 60).toInt()
        val seconds = ((((totalSeconds % 31536000) % 86400) % 3600) % 60).toInt()

        return arrayListOf(years, days, hours, minutes, seconds, totalSeconds.toInt())
    }

    private fun setStartedAtDateTime(): String {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val currentDateTime = Calendar.getInstance().time
        val formattedCurrentDateTime = date.format(currentDateTime)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("startedAt", formattedCurrentDateTime)
            commit()
        }
        return formattedCurrentDateTime
    }
}

