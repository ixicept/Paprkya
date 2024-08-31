package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomTransactionDetail(
    val Campus: String,
    val RoomName: String,
    val StatusDetails: List<List<StatusDetail>>
): Parcelable
