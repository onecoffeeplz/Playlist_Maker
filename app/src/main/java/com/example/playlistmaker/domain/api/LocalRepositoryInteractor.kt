package com.example.playlistmaker.domain.api

interface LocalRepositoryInteractor {
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, defValue: String?): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
    fun contains(key: String): Boolean
}