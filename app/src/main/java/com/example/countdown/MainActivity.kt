package com.example.countdown

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
            val time = setCountdownTime()
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

    private fun setCountdownDate(): Array<Int> {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        { view, year, monthOfYear, dayOfMonth ->
            Toast.makeText(this, """$dayOfMonth - ${monthOfYear + 1} - $year""", Toast.LENGTH_LONG).show() },
            year, month, day)
        datePicker.show()

        return arrayOf(year, month, day)
    }

    private fun setCountdownTime(): Array<Int> {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.YEAR)
        val minute = c.get(Calendar.MONTH)

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            Toast.makeText(this, """$hourOfDay - $minute""", Toast.LENGTH_LONG).show() },
            hour, minute, false)
        timePicker.show()

        return arrayOf(hour, minute)
    }


    private fun setTimeTexts() {

    }
}
