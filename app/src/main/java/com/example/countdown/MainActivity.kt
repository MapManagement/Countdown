package com.example.countdown

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declaring variables that contain TextViews
        val yearsText: TextView = findViewById(R.id.yearsText)
        val daysText: TextView = findViewById(R.id.daysText)
        val hoursText: TextView = findViewById(R.id.hoursText)
        val minutesText: TextView = findViewById(R.id.minutesText)
        val secondsText: TextView = findViewById(R.id.secondsText)


        val FAB: FloatingActionButton = findViewById(R.id.floating_point)

        FAB.setOnClickListener {
            DatePickerDialog(this, datePicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            TimePickerDialog(this, timePicker, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                    true).show()
        }

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

    val datePicker = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setTimeTexts()
    }

    val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
    }

    private fun setTimeTexts() {
        val format = "HH:mm dd/MM/yyyy"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        yearsText.text = date.format(cal.time)
    }
}
