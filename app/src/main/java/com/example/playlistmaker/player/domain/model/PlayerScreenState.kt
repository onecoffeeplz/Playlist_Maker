package com.example.playlistmaker.player.domain.model

import java.text.SimpleDateFormat
import java.util.Locale

sealed class PlayerScreenState(val progressText: String, val isTrackFavorite: Boolean) {
    class Default :
        PlayerScreenState(SimpleDateFormat("mm:ss", Locale.getDefault()).format(0), false)

    class Prepared(isTrackFavorite: Boolean) :
        PlayerScreenState(SimpleDateFormat("mm:ss", Locale.getDefault()).format(0), isTrackFavorite)

    class Playing(progressText: String, isTrackFavorite: Boolean) :
        PlayerScreenState(progressText, isTrackFavorite)

    class Paused(progressText: String, isTrackFavorite: Boolean) :
        PlayerScreenState(progressText, isTrackFavorite)
}