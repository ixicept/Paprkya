package edu.bluejack24_1.papryka.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class TeachingDetail(
    val Session: String,
    val Topics: String,
    val SubTopics: List<SubTopic>
): Parcelable

@Parcelize
data class SubTopic(
    val Value: String
): Parcelable