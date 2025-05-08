package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistsState {
    data object Loading : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
    data object Empty : PlaylistsState
}