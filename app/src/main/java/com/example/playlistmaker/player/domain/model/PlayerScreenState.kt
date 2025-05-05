package com.example.playlistmaker.player.domain.model

import com.example.playlistmaker.media.domain.models.Playlist
import java.text.SimpleDateFormat
import java.util.Locale

sealed class PlayerScreenState(
    val progressText: String, val isTrackFavorite: Boolean,
    val allPlaylists: MutableList<Playlist>
) {
    class Default :
        PlayerScreenState(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(0),
            false, mutableListOf()
        )

    class Prepared(
        isTrackFavorite: Boolean,
        allPlaylists: MutableList<Playlist>
    ) :
        PlayerScreenState(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(0),
            isTrackFavorite, allPlaylists
        )

    class Playing(
        progressText: String,
        isTrackFavorite: Boolean,
        allPlaylists: MutableList<Playlist>
    ) :
        PlayerScreenState(progressText, isTrackFavorite, allPlaylists)

    class Paused(
        progressText: String,
        isTrackFavorite: Boolean,
        allPlaylists: MutableList<Playlist>
    ) :
        PlayerScreenState(progressText, isTrackFavorite, allPlaylists)

}