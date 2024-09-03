package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusDetail(
    val Assistant: String?,
    val ClassName: String?,
    val Description: String?,
    val TransactionDetailId: String?,
    val TransactionId: String?,
    var RoomName: String?,
): Parcelable {
    constructor(Description: String, RoomName: String) : this("", "", Description, "", "", RoomName)
}
