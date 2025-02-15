package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {

    private var playerState = PlayerState.DEFAULT

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = PlayerState.PREPARED
                onPrepared()
            }
            mediaPlayer.setOnCompletionListener {
                playerState = PlayerState.PREPARED
                onCompletion()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                playerState = PlayerState.ERROR
                true
            }
        } catch (e: Exception) {
            playerState = PlayerState.ERROR
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun playbackControl(callback: (PlayerState) -> Unit) {
        when (playerState) {
            PlayerState.DEFAULT, PlayerState.PLAYING -> {
                pausePlayer()
                callback(PlayerState.PAUSED)
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
                callback(PlayerState.PLAYING)
            }

            PlayerState.ERROR -> {
                callback(PlayerState.ERROR)
            }

        }
    }

    override fun currentPosition(): String = SimpleDateFormat(
        "mm:ss", Locale.getDefault()
    ).format(mediaPlayer.currentPosition)

}