package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Room(
    val Campus: String,
    val Capacity: Int,
    val Name: String,
    val Note: String,
    val RoomId: String,
): Parcelable
