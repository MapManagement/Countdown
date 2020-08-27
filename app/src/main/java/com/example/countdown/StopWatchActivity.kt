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
    var seconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        // getting shared preferences to set primary constructors and customized color
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        startedAt = sharedPref.getString("startedAt", "")
        isStopped = sharedPref.getBoolean("wasStopped", true)
        seconds = sharedPref.getInt("seconds", 0)
        val chosenColor = sharedPref.getString("chosenColor", "#e01c18")
        changeViewColor(chosenColor)

        // stop watch continues
        if (startedAt != "") {
            val timePeriodInSeconds = getTimePeriod(startedAt, seconds)
            startTimer(convertSeconds(timePeriodInSeconds), startedAt)
        }

        // creating navigation between different modes
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

        // FloatingActionButtons for mode navigation
        val openFAB: FloatingActionButton = findViewById(R.id.floating_point_stop_watch)
        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)

        // expands buttons fo navigation
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

        // starts or continues stop watch
        startFAB.setOnClickListener {
            if (startedAt != "") {
                if (isStopped) {
                    isStopped = false
                    with(sharedPref.edit()) {
                        putBoolean("wasStopped", false)
                        commit()
                    }
                    setStartedAtDateTime()
                    //val timePeriodInSeconds = getTimePeriod(startedAt, seconds)
                    //startTimer(convertSeconds(timePeriodInSeconds), startedAt)
                }
                else {
                    isStopped = true
                    with(sharedPref.edit()) {
                        putBoolean("wasStopped", true)
                        putInt("seconds", seconds)
                        commit()
                    }
                }
            }
            else {
                isStopped = false
                setStartedAtDateTime()
                //val timePeriodInSeconds = getTimePeriod(startedAt, seconds)
                //startTimer(convertSeconds(timePeriodInSeconds), startedAt)
            }
        }

        // resets stop watch
        resetFAB.setOnClickListener {
            isStopped = true
            startedAt = ""
            seconds = 0
            colorTextsWhite()
            resetTexts()
        }

        // opens color picker
        colorFAB.setOnClickListener {
            colorPicker()
        }
    }

    // function for time handling and textview changes
    private fun startTimer(timePeriod: ArrayList<Int>, chosenDateTime: String?) {
        colorTextsWhite()
        setTexts(timePeriod)
        val leftoverSeconds = seconds

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }


        val handler = Handler()
        handler.post(object: Runnable {
            override fun run() {
                    if (!isStopped) {
                        val newTimePeriod = convertSeconds(getTimePeriod(startedAt, leftoverSeconds))
                        setTexts(newTimePeriod)
                        seconds++
                    }
                    handler.postDelayed(this, 1000)
            }
        })
    }

    // changes color of textviews if time periods is equal to zero and sets time period
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

    // colors textviews white
    private fun colorTextsWhite() {
        yearsText.setTextColor(Color.parseColor("#ffffff"))
        daysText.setTextColor(Color.parseColor("#ffffff"))
        hoursText.setTextColor(Color.parseColor("#ffffff"))
        minutesText.setTextColor(Color.parseColor("#ffffff"))
        secondsText.setTextColor(Color.parseColor("#ffffff"))
    }

    // resets textviews
    private fun resetTexts() {
        yearsText.text = "0 YEARS"
        daysText.text = "0 DAYS"
        hoursText.text = "0 HOURS"
        minutesText.text = "0 MINUTES"
        secondsText.text = "0 SECONDS"
    }

    // turns time between now and start of stop watch into seconds
    private fun getTimePeriod(startedAt: String?, extraSeconds: Int): Long {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val currentDateTime = Calendar.getInstance().time

        val formattedCurrentDateTime = date.format(currentDateTime)

        fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

        val startedAtSeconds: Long = LocalDateTime.parse(startedAt).toMillis() / 1000
        val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

        return currentSeconds - startedAtSeconds + extraSeconds
    }

    // converts seconds into different time periods
    private fun convertSeconds(totalSeconds: Long): ArrayList<Int> {
        val years = (totalSeconds / 31536000).toInt()
        val days = ((totalSeconds % 31536000) / 86400).toInt()
        val hours = (((totalSeconds % 31536000) % 86400) / 3600).toInt()
        val minutes = ((((totalSeconds % 31536000) % 86400) % 3600) / 60).toInt()
        val seconds = ((((totalSeconds % 31536000) % 86400) % 3600) % 60).toInt()

        return arrayListOf(years, days, hours, minutes, seconds, totalSeconds.toInt())
    }

    // sets new start of stop watch
    private fun setStartedAtDateTime(){
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val currentDateTime = Calendar.getInstance().time
        val formattedCurrentDateTime = date.format(currentDateTime)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("startedAt", formattedCurrentDateTime)
            commit()
        }
        startedAt = formattedCurrentDateTime
    }

    // opens color picker view for changing layout
    private fun colorPicker() {
        val colorPicker = ColorPicker(this, 100, 100, 100, 100)
        colorPicker.show()
        colorPicker.enableAutoClose()
        colorPicker.setCallback { color ->
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color).toLowerCase()
            changeViewColor(hexColor)
        }
    }

    // changes layout
    private fun changeViewColor(color: String?) {
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

    // opens MainActivity
    private fun openActivity(activityString: String) {
        if ( activityString == "stopwatch") {
            val intent = Intent(this, MainActivity::class.java)
            this.finish()
            startActivity(intent)
        }
    }
}

