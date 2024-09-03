package edu.bluejack24_1.papryka.adapters

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import edu.bluejack24_1.papryka.R

@BindingAdapter("scheduleBackgroundBasedOnType")
fun setScheduleBackgroundBasedOnType(view: LinearLayout, status: String?) {
    status?.let {
        val colorRes = when (status) {
            "Teaching" -> R.color.android_green
            "College" -> R.color.pastel_pink
            else -> R.color.white
        }
        view.setBackgroundResource(colorRes)
    }
}