package edu.bluejack24_1.papryka.utils

import android.view.View
import android.widget.ProgressBar

object ProgressBarUtils {

    // Show the progress bar
    fun show(progressBar: ProgressBar?) {
        progressBar?.visibility = View.VISIBLE
    }

    // Hide the progress bar
    fun hide(progressBar: ProgressBar?) {
        progressBar?.visibility = View.GONE
    }

    // Optionally show with a delay
    fun showWithDelay(progressBar: ProgressBar?, delayMillis: Long) {
        progressBar?.postDelayed({
            progressBar.visibility = View.VISIBLE
        }, delayMillis)
    }

    // Optionally hide with a delay
    fun hideWithDelay(progressBar: ProgressBar?, delayMillis: Long) {
        progressBar?.postDelayed({
            progressBar.visibility = View.GONE
        }, delayMillis)
    }
}
