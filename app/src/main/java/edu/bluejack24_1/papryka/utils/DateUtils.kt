package edu.bluejack24_1.papryka.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
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

fun getDayOfWeek(dateFormat: String, dateString: String): Int {
    val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
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

fun showDateDialog(context: Context, etDate: TextView, dateFormatter: android.icu.text.SimpleDateFormat) {
    val newCalendar: android.icu.util.Calendar = android.icu.util.Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate: android.icu.util.Calendar = android.icu.util.Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            etDate.text = dateFormatter.format(newDate.time)
        },
        newCalendar.get(android.icu.util.Calendar.YEAR),
        newCalendar.get(android.icu.util.Calendar.MONTH),
        newCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
}



