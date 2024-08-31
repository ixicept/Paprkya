package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Correction(
    val ClassName: String,
    val StartDate: String,
    val EndDate: String,
    val IsManualVerified: Boolean,
    val Status: String,
    val Type: String
): Parcelable
