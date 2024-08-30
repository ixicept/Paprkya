package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayOfWeekSchedule(
    val day: String,
    val schedules: List<Schedule>
): Parcelable
