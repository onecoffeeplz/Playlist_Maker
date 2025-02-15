package com.example.playlistmaker.settings.data.impl

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.local.LocalDataSourceImpl.Companion.DARK_THEME_ENABLED
import com.example.playlistmaker.settings.domain.api.AppThemeRepository
import com.example.playlistmaker.settings.domain.api.LocalDataSource

class AppThemeRepositoryImpl(private var storage: LocalDataSource, private val context: Context):
    AppThemeRepository {

    private var darkTheme = false

    override fun getCurrentTheme(): Boolean {
        return if (storage.contains(DARK_THEME_ENABLED)) {
            storage.getBoolean(DARK_THEME_ENABLED, darkTheme)
        } else {
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    override fun applyTheme() {
        val isDarkTheme = getCurrentTheme()
        switchTheme(isDarkTheme)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        storage.putBoolean(DARK_THEME_ENABLED, darkTheme)
    }
}