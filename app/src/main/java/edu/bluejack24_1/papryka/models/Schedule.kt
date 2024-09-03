package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import edu.bluejack24_1.papryka.utils.getShiftNumber
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val Subject: String,
    val Class: String,
    val Day: Int,
    var ShiftCode: Float,
    val Shift: String,
    val Room: String,
    val Type: String,
    val CourseOutlineId: String,
): Parcelable{
    init {
        ShiftCode = getShiftNumber(Shift)
    }

    val filterDay: Int
        get() = when (Day) {
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5
            6 -> 6
            else -> 0
        }

    val filterShift: Double
        get() = when (ShiftCode) {
            1.0F -> 1.0
            2.0F -> 2.0
            3.0F -> 3.0
            4.0F -> 4.0
            5.0F -> 5.0
            6.0F -> 6.0
            else -> 0.0
        }

}