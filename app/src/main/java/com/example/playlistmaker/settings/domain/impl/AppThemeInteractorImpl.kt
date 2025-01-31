package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.AppThemeInteractor
import com.example.playlistmaker.settings.domain.api.AppThemeRepository

class AppThemeInteractorImpl(private val theme: AppThemeRepository): AppThemeInteractor {
    override fun getCurrentTheme(): Boolean {
        return theme.getCurrentTheme()
    }

    override fun applyTheme() {
        theme.applyTheme()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        theme.switchTheme(darkThemeEnabled)
    }
}