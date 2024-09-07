package edu.bluejack24_1.papryka.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import edu.bluejack24_1.papryka.R

object SnackBarUtils {
    fun showSnackBar(view: View, message: String, length: Int = Snackbar.LENGTH_LONG) {
        val snackbar = Snackbar.make(view, message, length)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + getNavigationBarHeight(view.context))

        snackbarView.layoutParams = params
        snackbar.show()
    }

    fun showSnackBarWithAction(
        view: View,
        message: String,
        actionText: String,
        length: Int = Snackbar.LENGTH_LONG,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, message, length).setAction(actionText, action)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + getNavigationBarHeight(view.context))

        snackbarView.layoutParams = params
        snackbar.show()
    }

    private fun getNavigationBarHeight(context: Context): Int {
        val bottomNavView: View = (context as Activity).findViewById(R.id.botNav)
        println(bottomNavView.height)
        return bottomNavView.height
    }
}
