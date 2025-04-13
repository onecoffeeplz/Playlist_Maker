package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerScreenState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {

    private var playerScreenState = PlayerScreenState.Default()

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                onPrepared()
            }
            mediaPlayer.setOnCompletionListener {
                onCompletion()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                playerScreenState = PlayerScreenState.Default()
                true
            }
        } catch (e: Exception) {
            playerScreenState = PlayerScreenState.Default()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): String = SimpleDateFormat(
        "mm:ss", Locale.getDefault()
    ).format(mediaPlayer.currentPosition)

}