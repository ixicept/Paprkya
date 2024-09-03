package edu.bluejack24_1.papryka.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getDateRange(timeframe: String): Pair<String, String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val startDate: String
    val endDate: String

    when (timeframe) {
        "daily" -> {
            startDate = sdf.format(calendar.time)
            endDate = startDate
            println("Start Date: $startDate")
            println("End Date: $endDate")
        }
        "weekly" -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            startDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            endDate = sdf.format(calendar.time)
            println("Start Date: $startDate")
            println("End Date: $endDate")
        }
        else -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            startDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            endDate = sdf.format(calendar.time)
            println("Start Date: $startDate")
            println("End Date: $endDate")

        }
    }

    return Pair(startDate, endDate)
}