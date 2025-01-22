package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun getSearchHistory(): MutableList<Track> {
        return repository.getSearchHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun checkConditionsAndAdd(
        track: Track,
        tracks: MutableList<Track>
    ): MutableList<Track> {
        return repository.checkConditionsAndAdd(track, tracks)
    }

    override fun saveToSearchHistory(tracks: MutableList<Track>) {
        repository.saveToSearchHistory(tracks)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}