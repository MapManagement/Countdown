package com.example.countdown

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val chosenDateTime = sharedPref.getString("chosenDateTime", "")

        /*val jsonFileData = readJSON("data")
        val chosenDateTime = jsonFileData.getString("chosenDateTime")*/

         if (chosenDateTime != "") {

             val formattedCurrentDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)
                     .format(Calendar.getInstance().time)

             fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

             val chosenSeconds: Long = LocalDateTime.parse(chosenDateTime).toMillis() / 1000
             val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

             val timePeriod = convertSeconds(chosenSeconds - currentSeconds)
             println(timePeriod)
             startTimer(timePeriod, chosenDateTime)

         }

        val FAB: FloatingActionButton = findViewById(R.id.floating_point)

        FAB.setOnClickListener {
            DatePickerDialog(this, datePicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            TimePickerDialog(this, timePicker, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                    true).show()
        }
    }

    private val datePicker = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val timePeriod = convertSeconds(getTimePeriod())

        val formattedChosenDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)
                .format(cal.time)
        startTimer(timePeriod, formattedChosenDateTime)
    }

    private val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
    }

    private fun startTimer(timePeriod: ArrayList<Int>, chosenDateTime: String?) {
        setTexts(timePeriod)

        Toast.makeText(this, chosenDateTime, Toast.LENGTH_SHORT).show()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("chosenDateTime", chosenDateTime)
            commit()
        }

        /*val jsonFileData = readJSON("data")
        val newJsonData = jsonFileData.put("chosenDateTime", chosenDateTime.toString())
        writeJSON("data", newJsonData)*/


        object : CountDownTimer((timePeriod[5] * 1000).toLong(), 1000){
            override fun onFinish() {
                secondsText.text = "0 Seconds"
            }

            override fun onTick(p0: Long) {
                val newTimePeriod = convertSeconds(getTimePeriod())
                setTexts(newTimePeriod)
            }
        }.start()
    }

    private fun getTimePeriod(): Long {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val date = SimpleDateFormat(format, Locale.GERMANY)
        val chosenDateTime = cal.time
        val currentDateTime = Calendar.getInstance().time

        val formattedChosenDateTime = date.format(chosenDateTime)
        val formattedCurrentDateTime = date.format(currentDateTime)

        fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) = atZone(zone).toInstant().toEpochMilli()

        val chosenSeconds: Long = LocalDateTime.parse(formattedChosenDateTime).toMillis() / 1000
        val currentSeconds: Long = LocalDateTime.parse(formattedCurrentDateTime).toMillis() / 1000

        return chosenSeconds - currentSeconds
    }

    private fun convertSeconds(totalSeconds: Long): ArrayList<Int> {
        val years = (totalSeconds / 31536000).toInt()
        val days = ((totalSeconds % 31536000) / 86400).toInt()
        val hours = (((totalSeconds % 31536000) % 86400) / 3600).toInt()
        val minutes = ((((totalSeconds % 31536000) % 86400) % 3600) / 60).toInt()
        val seconds = ((((totalSeconds % 31536000) % 86400) % 3600) % 60).toInt()

        return arrayListOf(years, days, hours, minutes, seconds, totalSeconds.toInt())
    }

    private fun setTexts(timePeriodArray: ArrayList<Int>) {
        yearsText.text = timePeriodArray[0].toString() + " Years"
        daysText.text = timePeriodArray[1].toString() + " Days"
        hoursText.text = timePeriodArray[2].toString() + " Hours"
        minutesText.text = timePeriodArray[3].toString() + " Minutes"
        secondsText.text = timePeriodArray[4].toString() + " Seconds"
    }

    private fun readJSON(fileName: String): JSONObject {
        val jsonData = File("$fileName.json").readText(Charsets.UTF_8)
        return JSONObject(jsonData)
    }

    private fun writeJSON(fileName: String, jsonObj: JSONObject) {
        val file: File = File(applicationContext.filesDir, "$fileName.json")
        file.writeText(jsonObj.toString())
    }

}
