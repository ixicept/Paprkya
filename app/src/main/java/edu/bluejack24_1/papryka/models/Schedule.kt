package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val Day: Int,
    val Subject: String,
    val Room: String,
    val Shift: String,
): Parcelable
