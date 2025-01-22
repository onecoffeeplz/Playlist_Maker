package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {

    private var playerState = STATE_DEFAULT

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun playbackControl(): Boolean {
        return when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                false
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                true
            }

            else -> false
        }
    }

    override fun currentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}