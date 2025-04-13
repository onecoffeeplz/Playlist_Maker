package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    val interactor: PlayerInteractor,
    val favoritesInteractor: FavoritesInteractor
) :
    ViewModel() {

    private var timerJob: Job? = null

    private val playerScreenState = MutableLiveData<PlayerScreenState>()
    fun observeState(): LiveData<PlayerScreenState> = playerScreenState

    private var isFavorite: Boolean = false

    init {
        preparePlayer(null, null)
    }

    fun preparePlayer(url: String?, track: Track?) {
        if (url != null && track != null) {
            viewModelScope.launch {
                isTrackFavorite(track.trackId)
                interactor.preparePlayer(
                    url,
                    { playerScreenState.value = PlayerScreenState.Prepared(isFavorite) },
                    { playerScreenState.value = PlayerScreenState.Prepared(isFavorite) })
            }
        } else {
            playerScreenState.value = PlayerScreenState.Default()
        }
    }

    private suspend fun isTrackFavorite(trackId: Int) {
        isFavorite = favoritesInteractor.isInFavorites(trackId)
    }

    private fun onStartPlayer() {
        interactor.startPlayer()
        playerScreenState.postValue(
            PlayerScreenState.Playing(
                interactor.currentPosition(),
                isFavorite
            )
        )
        updateListenProgress()
    }

    fun onPausePlayer() {
        interactor.pausePlayer()
        timerJob?.cancel()
        playerScreenState.postValue(
            PlayerScreenState.Paused(
                interactor.currentPosition(),
                isFavorite
            )
        )
    }

    fun onRelease() {
        interactor.release()
        timerJob?.cancel()
        playerScreenState.value = PlayerScreenState.Default()
    }

    private fun updateListenProgress() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TIMER_UPDATE_TIME)
                if (playerScreenState.value is PlayerScreenState.Playing) {
                    playerScreenState.postValue(
                        (PlayerScreenState.Playing(
                            interactor.currentPosition(),
                            isFavorite
                        ))
                    )
                }
            }
        }
    }

    fun playbackControl() {
        when (playerScreenState.value) {
            is PlayerScreenState.Playing -> {
                onPausePlayer()
            }

            is PlayerScreenState.Prepared, is PlayerScreenState.Paused -> {
                onStartPlayer()
            }

            else -> {
                playerScreenState.postValue(PlayerScreenState.Prepared(isFavorite))
            }
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (favoritesInteractor.isInFavorites(track.trackId)) {
                favoritesInteractor.removeFromFavorites(track)
                isFavorite = false
            } else {
                favoritesInteractor.addToFavorites(track)
                isFavorite = true
            }
            playerScreenState.postValue(
                when (playerScreenState.value) {
                    is PlayerScreenState.Playing -> PlayerScreenState.Playing(
                        interactor.currentPosition(),
                        isFavorite
                    )

                    is PlayerScreenState.Paused -> PlayerScreenState.Paused(
                        interactor.currentPosition(),
                        isFavorite
                    )

                    else -> PlayerScreenState.Prepared(isFavorite)
                }
            )
        }
    }

    companion object {
        private const val TIMER_UPDATE_TIME = 300L
    }
}