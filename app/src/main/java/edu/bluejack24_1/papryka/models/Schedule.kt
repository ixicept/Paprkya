package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val course: String,
    val day: Int,
    val shift: Float?,
    val time: String,
    val room: String,
    val type: String
): Parcelable
