package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getSearchHistory(): MutableList<Track>
    fun addTrack(track: Track)
    fun checkConditionsAndAdd(track: Track, tracks: MutableList<Track>) : MutableList<Track>
    fun saveToSearchHistory(tracks: MutableList<Track>)
    fun clearSearchHistory()
}