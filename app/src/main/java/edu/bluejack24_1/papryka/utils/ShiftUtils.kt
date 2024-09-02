package edu.bluejack24_1.papryka.utils

import android.content.Context
import edu.bluejack24_1.papryka.R

fun getShiftNumber(time: String): Float {
    return when (time) {
        "07:20 - 09:00" -> 1.0F
        "09:20 - 11:00" -> 2.0F
        "11:20 - 13:00" -> 3.0F
        "13:20 - 15:00" -> 4.0F
        "15:20 - 17:00" -> 5.0F
        "17:20 - 19:00" -> 6.0F
        else -> 0.0F
    }
}

fun getDayFromInt(context: Context, dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> context.getString(R.string.monday)
        2 -> context.getString(R.string.tuesday)
        3 -> context.getString(R.string.wednesday)
        4 -> context.getString(R.string.thursday)
        5 -> context.getString(R.string.friday)
        6 -> context.getString(R.string.saturday)
        7 -> context.getString(R.string.sunday)
        else -> context.getString(R.string.invalid_day)
    }
}