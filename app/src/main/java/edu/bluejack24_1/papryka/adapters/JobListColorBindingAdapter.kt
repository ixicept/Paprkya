package edu.bluejack24_1.papryka.adapters

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import edu.bluejack24_1.papryka.R

@BindingAdapter("backgroundBasedOnStatus")
fun setBackgroundBasedOnStatus(view: LinearLayout, status: String?) {
    status?.let {
        val colorRes = when (status) {
            "Not Done" -> R.color.white
            "Deadline" -> R.color.cinnabar
            "Waiting for approval" -> R.color.green_mindaro
            "Approved" -> R.color.olive_drab
            else -> R.color.white
        }
        view.setBackgroundResource(colorRes)
    }
}