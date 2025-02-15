package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data class History(val tracks: List<Track>) : SearchState
    object NothingFound : SearchState
    data class Error(val error: ErrorType) : SearchState
}