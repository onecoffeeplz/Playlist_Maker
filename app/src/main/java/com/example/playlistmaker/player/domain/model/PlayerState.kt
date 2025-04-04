package com.example.playlistmaker.player.domain.model

import java.text.SimpleDateFormat
import java.util.Locale

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val progress: String) {
    class Default : PlayerState(false, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0))
    class Prepared : PlayerState(true, SimpleDateFormat("mm:ss", Locale.getDefault()).format(0))
    class Playing(progress: String) : PlayerState(true, progress)
    class Paused(progress: String) : PlayerState(true, progress)
}