package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistDetailsState {
    data object Loading : PlaylistDetailsState
    data class Content(val playlist: Playlist, val tracks: List<Track>) : PlaylistDetailsState
}