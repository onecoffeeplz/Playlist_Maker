package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.settings.domain.api.LocalDataSource
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private var storage: LocalDataSource, private val gson: Gson) :
    SearchHistoryRepository {

    override fun getSearchHistory(): MutableList<Track> {
        val json = storage.getString(SEARCH_HISTORY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun addTrack(track: Track) {
        val savedTracks = getSearchHistory()
        saveToSearchHistory(checkConditionsAndAdd(track, savedTracks))
    }

    override fun checkConditionsAndAdd(
        track: Track,
        tracks: MutableList<Track>
    ): MutableList<Track> {
        if (tracks.contains(track)) tracks.remove(track)
        if (tracks.size >= MAX_SEARCH_HISTORY) tracks.remove(tracks.last())
        tracks.add(0, track)
        return tracks
    }

    override fun saveToSearchHistory(tracks: MutableList<Track>) {
        val json = gson.toJson(tracks)
        storage.putString(SEARCH_HISTORY, json)
    }

    override fun clearSearchHistory() {
        storage.remove(SEARCH_HISTORY)
    }

    companion object {
        private const val MAX_SEARCH_HISTORY = 10
        const val SEARCH_HISTORY = "search_history"
    }
}