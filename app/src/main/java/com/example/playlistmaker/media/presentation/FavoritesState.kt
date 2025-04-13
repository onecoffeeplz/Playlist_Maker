package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {
    object Loading : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    object Empty : FavoritesState
}