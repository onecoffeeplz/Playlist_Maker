package com.example.playlistmaker.player.domain.model

import java.text.SimpleDateFormat
import java.util.Locale

sealed class PlayerState(val progressText: String) {
    class Default : PlayerState(SimpleDateFormat("mm:ss", Locale.getDefault()).format(0))
    class Prepared : PlayerState( SimpleDateFormat("mm:ss", Locale.getDefault()).format(0))
    class Playing(progressText: String) : PlayerState(progressText)
    class Paused(progressText: String) : PlayerState(progressText)
}