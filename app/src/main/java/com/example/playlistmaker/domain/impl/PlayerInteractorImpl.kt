package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val player: PlayerRepository): PlayerInteractor {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        player.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        player.startPlayer()
    }

    override fun pausePlayer() {
        player.pausePlayer()
    }

    override fun release() {
        player.release()
    }

    override fun playbackControl(): Boolean {
        return player.playbackControl()
    }

    override fun currentPosition() : Long {
        return player.currentPosition()
    }


}