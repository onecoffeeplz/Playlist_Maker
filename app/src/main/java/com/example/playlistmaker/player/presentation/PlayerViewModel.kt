package com.example.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(val interactor: PlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val playerState = MutableLiveData<PlayerState>()
    private val _listenProgress = MutableLiveData<String>()
    val listenProgress: LiveData<String> get() = _listenProgress

    private var updateProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            _listenProgress.value = interactor.currentPosition()
            handler.postDelayed(this, TIMER_UPDATE_TIME)
        }
    }

    fun observeState(): LiveData<PlayerState> = playerState

    init {
        interactor.playbackControl { state ->
            playerState.postValue(state)
        }
    }

    fun preparePlayer(url: String?) {
        if (url != null) {
            interactor.preparePlayer(
                url,
                { playerState.postValue(PlayerState.PREPARED) },
                {
                    playerState.postValue(PlayerState.DEFAULT)
                    handler.removeCallbacks(updateProgressRunnable)
                    _listenProgress.value = "0:00"
                })
        } else {
            playerState.postValue(PlayerState.ERROR)
            handler.removeCallbacks(updateProgressRunnable)
        }
    }

    private fun onStartPlayer() {
        interactor.startPlayer()
        handler.post(updateProgressRunnable)
        playerState.postValue(PlayerState.PLAYING)
    }

    fun onPausePlayer() {
        interactor.pausePlayer()
        handler.removeCallbacks(updateProgressRunnable)
        playerState.postValue(PlayerState.PAUSED)
    }

    fun onRelease() {
        handler.removeCallbacks(updateProgressRunnable)
        interactor.release()
    }

    fun playbackControl() {
        interactor.playbackControl { state ->
            handler.removeCallbacks(updateProgressRunnable)
            when (state) {
                PlayerState.PREPARED -> {
                    handler.post(updateProgressRunnable)
                    playerState.postValue(PlayerState.PREPARED)
                }

                PlayerState.PLAYING -> {
                    onStartPlayer()
                }

                PlayerState.PAUSED -> {
                    onPausePlayer()
                }

                PlayerState.ERROR -> {
                    playerState.postValue(PlayerState.ERROR)
                }

                else -> {
                    playerState.postValue(PlayerState.DEFAULT)
                }
            }
        }
    }

    companion object {
        private const val TIMER_UPDATE_TIME = 500L
    }
}