package edu.bluejack24_1.papryka.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

fun getCurrentLanguage(context: Context): String? {
    val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val savedLanguage = sharedPref.getString("selected_language", "not_set")

    return if (savedLanguage == "not-set") {
        Locale.getDefault().language
    } else {
        savedLanguage
    }
}

fun getLanguageCode(language: String): String {
    return when (language) {
        "English" -> "en"
        "Bahasa Indonesia" -> "in"
        else -> "not-set"
    }
}

fun setLanguageForApp(activity: Activity, languageToLoad: String) {
    val locale = if (languageToLoad == "not-set") {
        Locale.getDefault()
    } else {
        Locale(languageToLoad)
    }

    Locale.setDefault(locale)
    val config = Configuration()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocale(locale)
    } else {
        @Suppress("DEPRECATION")
        config.locale = locale
    }

    activity.apply {
        resources.updateConfiguration(config, resources.displayMetrics)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            applicationContext.resources.updateConfiguration(config, applicationContext.resources.displayMetrics)
        }
    }

    val sharedPref = activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("selected_language", languageToLoad)
        apply()
    }
}