package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun playbackControl(): Boolean
    fun currentPosition(): Long
}