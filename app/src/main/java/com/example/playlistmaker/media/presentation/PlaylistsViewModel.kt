package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: NewPlaylistInteractor
) : ViewModel() {

    private val playlistsState = MutableLiveData <PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = playlistsState

    private val playlists: MutableList<Playlist> = mutableListOf()

    init {
        getAllPlaylists()
    }

    fun getAllPlaylists() {
        playlistsState.postValue(PlaylistsState.Loading)

        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlists -> processResult(playlists)
            }
        }
    }

    private fun processResult(allPlaylists: List<Playlist>?) {
        val playlistsResult = allPlaylists ?: emptyList()
        if (playlistsResult.isEmpty()) {
            playlistsState.postValue(PlaylistsState.Empty)
        } else {
            playlists.clear()
            playlists.addAll(playlistsResult)
            playlistsState.postValue(PlaylistsState.Content(playlists))
        }
    }

}