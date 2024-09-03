package edu.bluejack24_1.papryka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Casemaking(
    val Description: String,
    val StartDate: String,
    val EndDate: String,
    val Status: String,
    val JobType: String,
    var Variation: String,
    var Type: String
) : Parcelable {
    val isCaseMaking: Boolean
        get() = JobType == "Case Making"

    //masi error
    fun getDescription(description: String): String {
        println(description)
        val words = description.split(" ")
        println(words)
        return if (words.size >= 2) {
            words[words.size - 2]
        } else {
            "Unknown"
        }
    }

    fun getVariation(description: String): String {
        val words = description.split(" ")
        return if (words.isNotEmpty()) {
            words.last()
        } else {
            "Unknown"
        }
    }

}