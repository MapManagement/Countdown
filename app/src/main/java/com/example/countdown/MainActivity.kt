package com.example.countdown

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.util.*

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

        val FAB: FloatingActionButton = findViewById(R.id.floating_point)

        FAB.setOnClickListener {
            val date = setCountdownDate()
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

    private fun setCountdownDate() {
        val now = Calendar.getInstance()

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        },
            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun setCountdownTime() {
        val now = Calendar.getInstance()

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
        },
            now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false)
        timePicker.show()
    }


    private fun setTimeTexts() {

    }
}
