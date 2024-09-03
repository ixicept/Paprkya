package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollegeDetail(
    val CourseCode: String,
    val CourseName: String,
    val Shift: Int,
    val StartDate: String,
    val Room: String
): Parcelable

