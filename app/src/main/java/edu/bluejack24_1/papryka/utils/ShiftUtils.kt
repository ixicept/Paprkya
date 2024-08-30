package edu.bluejack24_1.papryka.utils

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

fun getDayFromInt(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Invalid day"
    }
}
