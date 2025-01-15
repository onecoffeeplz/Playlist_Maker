package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun playbackControl(): Boolean
    fun currentPosition(): Long
}