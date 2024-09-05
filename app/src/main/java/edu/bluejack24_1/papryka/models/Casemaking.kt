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

}