package com.example.countdown

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class StopWatchActivity : AppCompatActivity() {

    var currentColor: String? = "#e01c18"
    var startedAt: String? = ""

    var isStopped: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        startedAt = sharedPref.getString("startedAt", "")
        isStopped = sharedPref.getBoolean("wasStopped", false)

        bottom_navigation.setOnNavigationItemSelectedListener{
            when(it.itemId) {
                R.id.menu_datetimer -> {
                    openActivity("datetime")
                    true
                }
                R.id.menu_stop_watch -> {
                    openActivity("stopwatch")
                    true
                }
                else -> false
            }
        }

        val openFAB: FloatingActionButton = findViewById(R.id.floating_point_stop_watch)
        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)

        openFAB.setOnClickListener {
            if(startFAB.visibility != View.VISIBLE) {
                startFAB.visibility = View.VISIBLE
                resetFAB.visibility = View.VISIBLE
                colorFAB.visibility = View.VISIBLE
            }
            else {
                startFAB.visibility = View.INVISIBLE
                resetFAB.visibility = View.INVISIBLE
                colorFAB.visibility = View.INVISIBLE
            }
        }

        startFAB.setOnClickListener {
            if (startedAt != "") {
                if (isStopped) {
                    val timePeriodinSeconds = getTimePeriod(startedAt)
                    startTimer(convertSeconds(timePeriodinSeconds), startedAt)
                }
                else{
                    isStopped = true
                }
            }
            else {
                val newStartedAt = setStartedAtDateTime()
                val timePeriodinSeconds = getTimePeriod(newStartedAt)
                startTimer(convertSeconds(timePeriodinSeconds), newStartedAt)
            }
        }

        resetFAB.setOnClickListener {
            isStopped = true
            startedAt = ""
            colorTextsWhite()
        }

        colorFAB.setOnClickListener {
            colorPicker()
        }
    }

    private fun startTimer(timePeriod: ArrayList<Int>, chosenDateTime: String?) {
        colorTextsWhite()
        setTexts(timePeriod)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }

        val handler = Handler()
        handler.post(object: Runnable {
            override fun run() {
                val newTimePeriod = convertSeconds(getTimePeriod(startedAt))
                setTexts(newTimePeriod)
                handler.postDelayed(this, 1000)
            }
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

    private fun colorPicker() {
        val colorPicker = ColorPicker(this, 100, 100, 100, 100)
        colorPicker.show()
        colorPicker.enableAutoClose()
        colorPicker.setCallback { color ->
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color).toLowerCase()
            changeViewColor(hexColor)
        }
    }

    private fun changeViewColor(color: String?) {
        print(color)
        currentColor = color

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenColor", color)
            commit()
        }

        val openFAB: FloatingActionButton = findViewById(R.id.floating_point_stop_watch)
        openFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color))
        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        startFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color) + 75)
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        resetFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color) + 150)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        colorFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color) + 225)
    }

    private fun openActivity(activityString: String) {
        val intent = when(activityString) {
            "datetime" -> Intent(this, MainActivity::class.java)
            "stopwatch" -> Intent(this, StopWatchActivity::class.java)
            else -> Intent(this, StopWatchActivity::class.java)
        }
        this.finish()
        startActivity(intent)
    }
}

