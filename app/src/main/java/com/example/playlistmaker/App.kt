package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    fun getCurrentTheme(): Boolean {
        return if (sharedPreferences.contains(DARK_THEME_ENABLED)) {
            sharedPreferences.getBoolean(DARK_THEME_ENABLED, darkTheme)
        } else {
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun applyTheme() {
        val isDarkTheme = getCurrentTheme()
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit().putBoolean(DARK_THEME_ENABLED, darkTheme).apply()
    }
}