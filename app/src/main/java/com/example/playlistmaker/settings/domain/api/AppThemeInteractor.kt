package com.example.playlistmaker.settings.domain.api

interface AppThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun applyTheme()
    fun switchTheme(darkThemeEnabled: Boolean)
}