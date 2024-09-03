package edu.bluejack24_1.papryka.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getDateRange(timeframe: String): Pair<String, String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance(Locale.GERMANY)

    val startDate: String
    val endDate: String

    when (timeframe) {
        "daily" -> {
            startDate = sdf.format(calendar.time)
            endDate = startDate
        }
        "weekly" -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            startDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            endDate = sdf.format(calendar.time)
        }
        else -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            startDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            endDate = sdf.format(calendar.time)

        }
    }

    return Pair(startDate, endDate)
}

fun getDayOfWeek(dateString: String): Int {
    val format = SimpleDateFormat("M/d/yyyy hh:mm:ss a", Locale.ENGLISH)
    val date = format.parse(dateString)

    val calendar = Calendar.getInstance()
    if (date != null) {
        calendar.time = date
    }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    return when (dayOfWeek) {
        Calendar.SUNDAY -> 7
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        else -> 1
    }
}