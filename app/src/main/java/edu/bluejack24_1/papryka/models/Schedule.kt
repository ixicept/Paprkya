package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val Subject: String,
    val Day: Int,
    val ShiftCode: Float,
    val Shift: String,
    val Room: String,
    val Type: String,
): Parcelable
