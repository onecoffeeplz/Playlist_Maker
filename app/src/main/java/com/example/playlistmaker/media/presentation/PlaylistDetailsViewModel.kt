package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistDetailsViewModel(
    val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistDetailsState = MutableLiveData<PlaylistDetailsState>()
    fun observeState(): LiveData<PlaylistDetailsState> = playlistDetailsState

    suspend fun getPlaylistDetails(playlistId: Int) {
        playlistDetailsState.postValue(PlaylistDetailsState.Loading)

        playlistInteractor.getPlaylistDetails(playlistId).let { pair ->
            playlistDetailsState.postValue(PlaylistDetailsState.Content(pair.first, pair.second))
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
                val pair = playlistInteractor.getPlaylistDetails(playlistId)
                pair
            }.let { pair ->
                playlistDetailsState.postValue(PlaylistDetailsState.Content(pair.first, pair.second))
            }
        }
    }

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        playlistInteractor.sharePlaylist(playlist, tracks)
    }

}