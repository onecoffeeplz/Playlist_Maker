package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val newPlaylistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlistCreationStatus = MutableLiveData<Result<Unit>>()
    val playlistCreationStatus: LiveData<Result<Unit>> get() = _playlistCreationStatus

    fun createPlaylist(name: String, description: String, coverUri: String?) {
        viewModelScope.launch {
            try {
                var localCoverUri: String? = null
                if (coverUri != null) {
                    localCoverUri = newPlaylistInteractor.copyPlaylistCoverToLocalStorage(coverUri)
                }
                val playlist = Playlist(
                    playlistName = name,
                    playlistDescription = description,
                    playlistCoverUri = localCoverUri)
                newPlaylistInteractor.createPlaylist(playlist)
                _playlistCreationStatus.postValue(Result.success(Unit))
            } catch (e: Exception) {
                _playlistCreationStatus.value = Result.failure(e)
            }
        }
    }
}