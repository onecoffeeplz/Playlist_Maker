package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(val interactor: PlayerInteractor) : ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = playerState

    init {
        preparePlayer(null)
    }

    fun preparePlayer(url: String?) {
        if (url != null) {
            viewModelScope.launch {
                interactor.preparePlayer(
                    url,
                    { playerState.value = PlayerState.Prepared() },
                    { playerState.value = PlayerState.Prepared() })
            }
        } else {
            playerState.value = PlayerState.Default()
        }
    }

    private fun onStartPlayer() {
        interactor.startPlayer()
        playerState.postValue(PlayerState.Playing(interactor.currentPosition()))
        updateListenProgress()
    }

    fun onPausePlayer() {
        interactor.pausePlayer()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(interactor.currentPosition()))
    }

    fun onRelease() {
        interactor.release()
        timerJob?.cancel()
        playerState.value = PlayerState.Default()
    }

    private fun updateListenProgress() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TIMER_UPDATE_TIME)
                if (playerState.value is PlayerState.Playing) {
                    playerState.postValue((PlayerState.Playing(interactor.currentPosition())))
                }
            }
        }
    }

    fun playbackControl() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                onPausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                onStartPlayer()
            }

            else -> {
                playerState.postValue(PlayerState.Prepared())
            }
        }
    }

    companion object {
        private const val TIMER_UPDATE_TIME = 300L
    }
}