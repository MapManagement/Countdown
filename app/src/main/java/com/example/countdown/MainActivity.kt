package com.example.countdown

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var gestureListener: GestureDetector
    var x_start: Float = 0.0f
    var x_end: Float = 0.0f
    var y_start: Float = 0.0f
    var y_end: Float = 0.0f

    var cal: Calendar = Calendar.getInstance()
    var currentTimer: CountDownTimer? = null
    var currentColor: String? = "#e01c18"
    var currentPictureUri: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureListener = GestureDetector(this, this)

        window.navigationBarColor = Color.BLACK

        // getting shared preferences to set primary constructors and customized color
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val chosenDateTime = sharedPref.getString("chosenDateTime", "")

        val chosenPictureUri = sharedPref.getString("pictureURI", "")
        val stopWatchActivityPictureUri = intent.getStringExtra("currentPictureUri")
        if (stopWatchActivityPictureUri != "" && stopWatchActivityPictureUri != null) {
            currentPictureUri = stopWatchActivityPictureUri
            setBackground()
        }
        else if (chosenPictureUri != "" && chosenPictureUri != null) {
            currentPictureUri = chosenPictureUri
            setBackground()
        }

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

            val timePeriod = TimeCalculations().convertSeconds(chosenSeconds - currentSeconds)
            val timer = startTimer(timePeriod, chosenDateTime)
            currentTimer = timer
            timer.start()
        }

        // FloatingActionButtons for mode navigation
        val timeFAB: FloatingActionButton = findViewById(R.id.floating_point_time)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)

        // sets new date time
        timeFAB.setOnClickListener{ chooseNewDateTime() }
        // opens color picker
        colorFAB.setOnClickListener{ colorPicker() }
        // open image picker (gallery)
        imageFAB.setOnClickListener { openGallery() }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureListener.onTouchEvent(event)

        when (event?.action) {
            0 ->
            {
                x_start = event.x
                y_start = event.y
            }
            1 ->
            {
                x_end = event.x
                y_end = event.y

                val distanceFloatX: Float = x_end- x_start
                val distanceFloatY: Float = y_end - y_start
                checkSwipeGesture(distanceFloatX, distanceFloatY)
            }

        }
        return super.onTouchEvent(event)
    }

    private fun checkSwipeGesture(distanceFloatX: Float, distanceFloatY: Float) {
        if (abs(distanceFloatX) > 300) {
            if (x_end <= x_start){
                openActivity("stopwatch")
            }
        }
        else if (abs(distanceFloatY) > 300) {
            val timeFAB: FloatingActionButton = findViewById(R.id.floating_point_time)
            val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
            val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)
            val dateTimerTitle: TextView = findViewById(R.id.title_datetimer)
            val stopWatchTitle: TextView = findViewById(R.id.title_stopwatch)
            val elements = listOf(timeFAB, colorFAB, imageFAB, dateTimerTitle, stopWatchTitle)

            if (y_end > y_start && timeFAB.isVisible) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.fab_fade_out)
                for (element in elements) {
                    element.startAnimation(animation)
                    element.visibility = View.INVISIBLE
                }
            }
            else if (y_end < y_start && !timeFAB.isVisible) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.fab_fade_in)
                for (element in elements) {
                    element.startAnimation(animation)
                    element.visibility = View.VISIBLE
                }
            }
        }
    }

    // datepicker dialog to choose new date, initializes timer
    @RequiresApi(Build.VERSION_CODES.O)
    private val datePicker = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val formattedChosenDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)
                .format(cal.time)
        val timePeriod = TimeCalculations().convertSeconds(TimeCalculations().getTimePeriod(formattedChosenDateTime))

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
        checkTimeSpans(timePeriod)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }

        val timer = object : CountDownTimer((timePeriod[5] * 1000).toLong(), 1000){
            override fun onFinish() {
                secondsNumber.text = "00"
                checkTimeSpans(arrayListOf(0,0,0,0,0,0))
            }

            override fun onTick(p0: Long) {
                val test = TimeCalculations().convertSeconds(TimeCalculations().getTimePeriod(chosenDateTime))
                val newTimePeriod = TimeCalculations().convertSeconds(TimeCalculations().getTimePeriod(chosenDateTime))
                if (newTimePeriod[5] < 0) {
                    onFinish()
                }
                checkTimeSpans(newTimePeriod)
            }

        }
        return timer
    }

    // changes color of textviews if time periods is equal to zero and sets time period
    private fun checkTimeSpans(timePeriodArray: ArrayList<Int>) {
        val totalSeconds = timePeriodArray[5]
        if (totalSeconds < 31536000) {
            yearsText.setTextColor(Color.parseColor(currentColor))
            yearsNumber.setTextColor(Color.parseColor(currentColor))
            if (totalSeconds < 86400) {
                daysText.setTextColor(Color.parseColor(currentColor))
                daysNumber.setTextColor(Color.parseColor(currentColor))
                if (totalSeconds < 3600) {
                    hoursText.setTextColor(Color.parseColor(currentColor))
                    hoursNumber.setTextColor(Color.parseColor(currentColor))
                    if (totalSeconds < 60) {
                        minutesText.setTextColor(Color.parseColor(currentColor))
                        minutesNumber.setTextColor(Color.parseColor(currentColor))
                    }
                }
            }
        }
        setViewTexts(timePeriodArray)
    }

    private fun setViewTexts(timePeriodArray: ArrayList<Int>) {
        yearsNumber.text = String.format("%02d", timePeriodArray[0])
        daysNumber.text = String.format("%02d", timePeriodArray[1])
        hoursNumber.text = String.format("%02d", timePeriodArray[2])
        minutesNumber.text = String.format("%02d", timePeriodArray[3])
        secondsNumber.text = String.format("%02d", timePeriodArray[4])
    }

    // colors textviews white
    private fun colorTextsWhite() {
        yearsText.setTextColor(Color.parseColor("#ffffff"))
        yearsNumber.setTextColor(Color.parseColor("#ffffff"))
        daysText.setTextColor(Color.parseColor("#ffffff"))
        daysNumber.setTextColor(Color.parseColor("#ffffff"))
        hoursText.setTextColor(Color.parseColor("#ffffff"))
        hoursNumber.setTextColor(Color.parseColor("#ffffff"))
        minutesText.setTextColor(Color.parseColor("#ffffff"))
        minutesNumber.setTextColor(Color.parseColor("#ffffff"))
        secondsText.setTextColor(Color.parseColor("#ffffff"))
        secondsNumber.setTextColor(Color.parseColor("#ffffff"))
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

        val timeFAB: FloatingActionButton = findViewById(R.id.floating_point_time)
        timeFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color))
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        colorFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color) + 75)
        val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)
        imageFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color) + 150)
        val dateTimerTitle: TextView = findViewById(R.id.title_datetimer)
        dateTimerTitle.setTextColor(Color.parseColor(color))

        colorTextsWhite()
    }

    // opens StopWatchActivity
    private fun openActivity(activityString: String) {
        if ( activityString == "stopwatch") {
            val intent = Intent(this, StopWatchActivity::class.java)
            intent.putExtra("currentColor", currentColor)
            intent.putExtra("currentPictureUri", currentPictureUri)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1803)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1803) {
            try {
                currentPictureUri = data?.data.toString()
                setBackground()
            }
            catch (e: Exception) {
                Toast.makeText(this,"Unexpected Error occured!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setBackground() {
        currentPictureUri?.toUri()?.let { getContentResolver().takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        imageView.setImageURI(currentPictureUri?.toUri())
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("pictureURI", currentPictureUri)
            commit()
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
