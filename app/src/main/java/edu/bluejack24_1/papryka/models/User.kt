package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class User(
    val UserId: String,
    val BinusianId: String,
    val Roles: List<String>,
    val Name: String,
    val Username: String,
    val BinusianNumber: String,
): Parcelable
