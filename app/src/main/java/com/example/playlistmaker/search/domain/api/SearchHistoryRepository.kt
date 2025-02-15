package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun addTrack(track: Track)
    fun checkConditionsAndAdd(track: Track, tracks: MutableList<Track>) : MutableList<Track>
    fun saveToSearchHistory(tracks: MutableList<Track>)
    fun clearSearchHistory()
}