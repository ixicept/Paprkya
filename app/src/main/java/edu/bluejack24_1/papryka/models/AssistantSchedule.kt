package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssistantSchedule(
    val initial: String,
    var schedule: Schedule
) : Parcelable
