package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun playbackControl(callback: (PlayerState) -> Unit)
    fun currentPosition(): String
}