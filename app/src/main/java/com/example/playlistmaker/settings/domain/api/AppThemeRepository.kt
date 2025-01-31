package com.example.playlistmaker.settings.domain.api

interface AppThemeRepository {
    fun getCurrentTheme(): Boolean
    fun applyTheme()
    fun switchTheme(darkThemeEnabled: Boolean)
}