package com.example.playlistmaker.media.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val newPlaylistInteractor: NewPlaylistInteractor) : ViewModel() {
    fun createPlaylist(name: String, description: String, coverUri: String?) {
        viewModelScope.launch {
            var localCoverUri: String? = null
            if (coverUri != null) {
                localCoverUri = newPlaylistInteractor.copyPlaylistCoverToLocalStorage(coverUri)
            }
            val playlist = Playlist(
                playlistName = name,
                playlistDescription = description,
                playlistCoverUri = localCoverUri)
            newPlaylistInteractor.createPlaylist(playlist)
        }
    }
}