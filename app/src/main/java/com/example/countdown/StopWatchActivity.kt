package com.example.countdown

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.abs


class StopWatchActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var gestureListener: GestureDetector
    var x_start: Float = 0.0f
    var x_end: Float = 0.0f
    var y_start: Float = 0.0f
    var y_end: Float = 0.0f

    var currentColor: String? = "#e01c18"
    var currentPictureUri: String? = ""
    var startedAt: String? = ""
    var isStopped: Boolean = true
    var oldSeconds: Int = 0
    var newSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        gestureListener = GestureDetector(this, this)

        window.navigationBarColor = Color.BLACK

        // getting shared preferences to set primary constructors and customized color
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        isStopped = sharedPref.getBoolean("wasStopped", true)
        oldSeconds = sharedPref.getInt("oldSeconds", 0)
        if (!isStopped) { startedAt = sharedPref.getString("startedAt", "") }

        val mainActivityPictureUri = intent.getStringExtra("currentPictureUri")
        val chosenPictureUri = sharedPref.getString("pictureUri", "")
        if (mainActivityPictureUri != "" && mainActivityPictureUri != null) {
            currentPictureUri = mainActivityPictureUri
            setBackground()
        }
        else if (chosenPictureUri != "" && chosenPictureUri != null) {
            currentPictureUri = chosenPictureUri
            setBackground()
        }

        changeViewColor(intent.getStringExtra("currentColor"))

        // stop watch continues
        if (startedAt == "") {
            setStartedAtDateTime()
        }
        val timePeriodInSeconds = getTimePeriod(startedAt, oldSeconds)
        startTimer(TimeCalculations().convertSeconds(timePeriodInSeconds), startedAt)

        // FloatingActionButtons for mode navigation
        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)

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
                    oldSeconds += newSeconds
                    newSeconds = 0
                    with(sharedPref.edit()) {
                        putBoolean("wasStopped", true)
                        putInt("oldSeconds", oldSeconds)
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
            oldSeconds = 0
            newSeconds = 0
            colorTextsWhite()
            resetTexts()
        }

        // opens color picker
        colorFAB.setOnClickListener {
            colorPicker()
        }

        // opens image picker (gallery)
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

                if (abs(distanceFloatX) > 300) {
                    if (x_end >= x_start){
                        openActivity("datetime")
                    }
                }
                else if (abs(distanceFloatY) > 300) {
                    val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
                    val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
                    val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
                    val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)
                    val dateTimerTitle: TextView = findViewById(R.id.title_datetimer)
                    val stopWatchTitle: TextView = findViewById(R.id.title_stopwatch)
                    val elements = listOf(startFAB, resetFAB, colorFAB, colorFAB, imageFAB,
                        dateTimerTitle,stopWatchTitle)

                    if (y_end > y_start && startFAB.isVisible) {
                        val animation = AnimationUtils.loadAnimation(this, R.anim.fab_fade_out)
                        for (element in elements) {
                            element.startAnimation(animation)
                            element.visibility = View.INVISIBLE
                        }
                    }
                    else if (y_end < y_start && !startFAB.isVisible) {
                        val animation = AnimationUtils.loadAnimation(this, R.anim.fab_fade_in)
                        for (element in elements) {
                            element.startAnimation(animation)
                            element.visibility = View.VISIBLE
                        }
                    }
                }
            }

        }
        return super.onTouchEvent(event)
    }

    // function for time handling and textview changes
    private fun startTimer(timePeriod: ArrayList<Long>, chosenDateTime: String?) {
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
                    if (!isStopped) {
                        val newTimePeriod = TimeCalculations().convertSeconds(getTimePeriod(startedAt, oldSeconds))
                        setTexts(newTimePeriod)
                        newSeconds++
                    }
                    handler.postDelayed(this, 1000)
            }
        })
    }

    // changes color of textviews if time periods is equal to zero and sets time period
    private fun setTexts(timePeriodArray: ArrayList<Long>) {
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

    // resets textviews
    private fun resetTexts() {
        yearsNumber.text = "00"
        daysNumber.text = "00"
        hoursNumber.text = "00"
        minutesNumber.text = "00"
        secondsNumber.text = "00"
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
        currentColor = color

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenColor", color)
            commit()
        }

        val startFAB: FloatingActionButton = findViewById(R.id.floating_point_start)
        startFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color))
        val resetFAB: FloatingActionButton = findViewById(R.id.floating_point_reset)
        resetFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color) + 75)
        val colorFAB: FloatingActionButton = findViewById(R.id.floating_point_color)
        colorFAB.backgroundTintList= ColorStateList.valueOf(Color.parseColor(color) + 150)
        val imageFAB: FloatingActionButton = findViewById(R.id.floating_point_image)
        imageFAB.backgroundTintList=ColorStateList.valueOf(Color.parseColor(color) + 225)
        val stopWatchTitle: TextView = findViewById(R.id.title_stopwatch)
        stopWatchTitle.setTextColor(Color.parseColor(color))

        colorTextsWhite()
    }

    // opens MainActivity
    private fun openActivity(activityString: String) {
        if ( activityString == "datetime") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("currentColor", currentColor)
            intent.putExtra("currentPictureUri", currentPictureUri)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
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

