package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState

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

    override fun playbackControl(callback: (PlayerState) -> Unit) {
        return player.playbackControl(callback)
    }

    override fun currentPosition() : String {
        return player.currentPosition()
    }


}