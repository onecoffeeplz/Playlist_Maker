package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val MAX_SEARCH_HISTORY = 10
const val SEARCH_HISTORY = "search_history"

class SearchHistory(private var sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun getSearchHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val savedTracks = getSearchHistory()
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