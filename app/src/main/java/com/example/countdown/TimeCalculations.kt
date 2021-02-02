package com.example.countdown

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class TimeCalculations {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimePeriod(chosenDateTime: String?): Long {
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
    fun convertSeconds(totalSeconds: Long): ArrayList<Long> {
        val years = (totalSeconds / 31536000)
        val days = ((totalSeconds % 31536000) / 86400)
        val hours = (((totalSeconds % 31536000) % 86400) / 3600)
        val minutes = ((((totalSeconds % 31536000) % 86400) % 3600) / 60)
        val seconds = ((((totalSeconds % 31536000) % 86400) % 3600) % 60)
        println(totalSeconds)

        return arrayListOf(years, days, hours, minutes, seconds, totalSeconds)
    }
}