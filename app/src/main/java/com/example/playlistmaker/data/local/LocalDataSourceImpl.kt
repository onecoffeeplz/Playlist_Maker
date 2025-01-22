package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.LocalDataSource

class LocalDataSourceImpl(private var sharedPreferences: SharedPreferences): LocalDataSource {
    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getString(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "pm_preferences"
        const val DARK_THEME_ENABLED = "dark_theme_enabled"
    }
}