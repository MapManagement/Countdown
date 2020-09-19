package com.example.countdown

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var gestureListener: GestureDetector
    var x_start: Float = 0.0f
    var x_end: Float = 0.0f

    var cal: Calendar = Calendar.getInstance()
    var currentTimer: CountDownTimer? = null
    var currentColor: String? = "#e01c18"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureListener = GestureDetector(this, this)

        window.navigationBarColor = Color.BLACK

        // getting shared preferences to set primary constructors and customized color
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val chosenDateTime = sharedPref.getString("chosenDateTime", "")

        val stopActivityColor = intent.getStringExtra("currentColor")
        if (stopActivityColor == null) {
            changeViewColor(sharedPref.getString("chosenColor", "#e01c18"))
        }
        else {
            changeViewColor(stopActivityColor)
        }


        // continues ongoing timer
        if (chosenDateTime != "") {
            val formattedCurrentDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)
                     .format(Calendar.getInstance().time)

            fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

            val chosenSeconds: Long = LocalDateTime.parse(chosenDateTime).toMillis() / 1000
            val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

            val timePeriod = convertSeconds(chosenSeconds - currentSeconds)
            val timer = startTimer(timePeriod, chosenDateTime)
            currentTimer = timer
            timer.start()
        }

        /* creating navigation between different modes
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
        */

        // FloatingActionButtons for mode navigation
        val openFAB: FloatingActionButton = findViewById(R.id.floating_point)
        val timeFAB: FloatingActionButton = findViewById(R.id.floating_point_time)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)

        // sets new date time
        timeFAB.setOnClickListener{ chooseNewDateTime() }
        // opens color picker
        colorFAB.setOnClickListener{ colorPicker() }
        // expands buttons fo navigation
        openFAB.setOnClickListener {
            if(timeFAB.visibility != View.VISIBLE) {
                timeFAB.visibility = View.VISIBLE
                colorFAB.visibility = View.VISIBLE
            }
            else {
                timeFAB.visibility = View.INVISIBLE
                colorFAB.visibility = View.INVISIBLE
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureListener.onTouchEvent(event)

        when (event?.action) {
            0 ->
            {
                x_start = event.x
            }
            1 ->
            {
                x_end = event.x

                val distanceFloatX: Float = x_end- x_start

                if (abs(distanceFloatX) > 200) {
                    if (x_end <= x_start){
                        openActivity("stopwatch")
                    }
                }
            }

        }
        return super.onTouchEvent(event)
    }

    // datepicker dialog to choose new date, initializes timer
    private val datePicker = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val formattedChosenDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)
                .format(cal.time)
        val timePeriod = convertSeconds(getTimePeriod(formattedChosenDateTime))

        val timer = startTimer(timePeriod, formattedChosenDateTime)
        currentTimer = timer
        colorTextsWhite()
        timer.start()
    }

    // timepicker dialog to choose new time
    private val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
    }

    // function for time handling and textview changes
    private fun startTimer(timePeriod: ArrayList<Int>, chosenDateTime: String?): CountDownTimer {
        setTexts(timePeriod)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }

        val timer = object : CountDownTimer((timePeriod[5] * 1000).toLong(), 1000){
            override fun onFinish() {
                secondsText.text = "0 SECONDS"
                setTexts(arrayListOf(0,0,0,0,0,0))
            }

            override fun onTick(p0: Long) {
                val newTimePeriod = convertSeconds(getTimePeriod(chosenDateTime))
                if (newTimePeriod[5] < 0) {
                    onFinish()
                }
                setTexts(newTimePeriod)
            }

        }
        return timer
    }

    // turns time between now and chosen date time into seconds
    private fun getTimePeriod(chosenDateTime: String?): Long {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val currentDateTime = Calendar.getInstance().time

        val formattedCurrentDateTime = date.format(currentDateTime)

        fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

        val chosenSeconds: Long = LocalDateTime.parse(chosenDateTime).toMillis() / 1000
        val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

        return chosenSeconds - currentSeconds
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

    // opens up pickers to choose new date and time
    private fun chooseNewDateTime() {
        if (currentTimer != null) {
            currentTimer?.cancel()
        }
        DatePickerDialog(this, datePicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()

        TimePickerDialog(this, timePicker, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
            true).show()
    }

    // opens color picker view for changing layout
    private fun colorPicker() {
        val colorPicker = ColorPicker(this, 100, 100, 100)
        colorPicker.show()
        colorPicker.enableAutoClose()
        colorPicker.setCallback { color ->
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color).toLowerCase()
            changeViewColor(hexColor)
        }
    }

    // changes layout
    private fun changeViewColor(color: String?) {
        print(color)
        currentColor = color

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenColor", color)
            commit()
        }

        val openFAB: FloatingActionButton = findViewById(R.id.floating_point)
        openFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color))
        val timeFAB: FloatingActionButton = findViewById(R.id.floating_point_time)
        timeFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color) + 75)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        colorFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color) + 150)

        colorTextsWhite()
    }

    // opens StopWatchActivity
    private fun openActivity(activityString: String) {
        if ( activityString == "stopwatch") {
            val intent = Intent(this, StopWatchActivity::class.java)
            intent.putExtra("currentColor", currentColor)
            this.finish()
            startActivity(intent)
        }
    }

    override fun onShowPress(e: MotionEvent?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
