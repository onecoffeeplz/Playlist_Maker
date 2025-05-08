package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoritesInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleEventLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    val interactor: PlayerInteractor,
    val favoritesInteractor: FavoritesInteractor,
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerScreenState = MutableLiveData<PlayerScreenState>()
    fun observeState(): LiveData<PlayerScreenState> = playerScreenState

    private val _onTrackAddTrigger = SingleEventLiveData<Pair<Boolean, String>>()
    fun onTrackAddTrigger(): LiveData<Pair<Boolean, String>> = _onTrackAddTrigger

    private var isFavorite: Boolean = false
    private var savedPlaylists: MutableList<Playlist> = mutableListOf()

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
                val newState = when (val currentState = playerScreenState.value) {
                    is PlayerScreenState.Playing -> PlayerScreenState.Playing(
                        currentState.progressText,
                        isFavorite,
                        savedPlaylists
                    )

                    is PlayerScreenState.Paused -> PlayerScreenState.Paused(
                        currentState.progressText,
                        isFavorite,
                        savedPlaylists
                    )

                    else -> PlayerScreenState.Prepared(
                        isFavorite,
                        savedPlaylists
                    )
                }
                playerScreenState.postValue(newState)
            }
        }
    }

    private fun processResult(allPlaylists: List<Playlist>?) {
        val playlistsResult = allPlaylists ?: emptyList()
        if (playlistsResult.isEmpty()) {
            savedPlaylists = mutableListOf()
        } else {
            savedPlaylists.clear()
            savedPlaylists.addAll(playlistsResult)
        }
    }

    fun preparePlayer(url: String?, track: Track?) {
        if (url != null && track != null) {
            viewModelScope.launch {
                isTrackFavorite(track.trackId)
                interactor.preparePlayer(
                    url,
                    {
                        playerScreenState.value =
                            PlayerScreenState.Prepared(isFavorite, savedPlaylists)
                    },
                    {
                        playerScreenState.value =
                            PlayerScreenState.Prepared(isFavorite, savedPlaylists)
                    })
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
                isFavorite,
                savedPlaylists
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
                isFavorite,
                savedPlaylists
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
                            isFavorite,
                            savedPlaylists
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
                playerScreenState.postValue(
                    PlayerScreenState.Prepared(
                        isFavorite,
                        savedPlaylists
                    )
                )
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
                        isFavorite,
                        savedPlaylists
                    )

                    is PlayerScreenState.Paused -> PlayerScreenState.Paused(
                        interactor.currentPosition(),
                        isFavorite,
                        savedPlaylists
                    )

                    else -> PlayerScreenState.Prepared(isFavorite, savedPlaylists)
                }
            )
        }
    }

    fun onAddToPlaylistClick(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistInteractor.addTrackToPlaylist(track, playlist)
            _onTrackAddTrigger.postValue(Pair(result, playlist.playlistName))
        }
    }

    companion object {
        private const val TIMER_UPDATE_TIME = 300L
    }
}