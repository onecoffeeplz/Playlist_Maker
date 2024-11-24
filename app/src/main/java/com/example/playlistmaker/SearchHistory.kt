package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val MAX_SEARCH_HISTORY = 10
const val SEARCH_HISTORY = "search_history"

object SearchHistory {

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences

    fun init(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    fun addTrack(track: Track) {
        val savedTracks = getSearchHistory().toMutableList()
        if (savedTracks.contains(track)) savedTracks.remove(track)
        if (savedTracks.size >= MAX_SEARCH_HISTORY) savedTracks.remove(savedTracks.last())
        savedTracks.add(0, track)
        saveToSearchHistory(savedTracks)
    }

    private fun saveToSearchHistory(tracks: MutableList<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(SEARCH_HISTORY, json).apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY).apply()
    }
}