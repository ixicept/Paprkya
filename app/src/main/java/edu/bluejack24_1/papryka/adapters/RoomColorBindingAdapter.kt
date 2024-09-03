package edu.bluejack24_1.papryka.adapters

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import edu.bluejack24_1.papryka.R

@BindingAdapter("roomBackgroundBasedOnStatus")
fun setRoomBackgroundBasedOnStatus(view: LinearLayout, description: String?) {
    description?.let {
        val colorRes = when (description) {
            "Available" -> R.color.white
            else -> R.color.android_green
        }
        view.setBackgroundResource(colorRes)
    }
}