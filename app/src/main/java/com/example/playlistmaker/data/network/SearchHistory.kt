package com.example.playlistmaker.data.network

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
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
        saveToSearchHistory(checkConditionsAndAdd(track, savedTracks))
    }

    fun checkConditionsAndAdd(track: Track, tracks: MutableList<Track>) : MutableList<Track> {
        if (tracks.contains(track)) tracks.remove(track)
        if (tracks.size >= MAX_SEARCH_HISTORY) tracks.remove(tracks.last())
        tracks.add(0, track)
        return tracks
    }

    private fun saveToSearchHistory(tracks: MutableList<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(SEARCH_HISTORY, json).apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY).apply()
    }
}