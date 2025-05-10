package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor

class PlaylistDetailsViewModel(
    val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val playlistDetailsState = MutableLiveData<PlaylistDetailsState>()
    fun observeState(): LiveData<PlaylistDetailsState> = playlistDetailsState

    suspend fun getPlaylistDetails(playlistId: Int) {
        playlistDetailsState.postValue(PlaylistDetailsState.Loading)

        playlistInteractor.getPlaylistDetails(playlistId).let { pair ->
            playlistDetailsState.postValue(PlaylistDetailsState.Content(pair.first, pair.second))
        }
    }


}