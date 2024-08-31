package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomTransaction(
    val Dates: List<String>,
    val Details: List<RoomTransactionDetail>,
): Parcelable
