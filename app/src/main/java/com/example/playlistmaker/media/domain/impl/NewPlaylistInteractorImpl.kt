package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.NewPlaylistInteractor
import com.example.playlistmaker.media.domain.db.NewPlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class NewPlaylistInteractorImpl(
    private val newPlaylistRepository: NewPlaylistRepository
) : NewPlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        return newPlaylistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return newPlaylistRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return newPlaylistRepository.getPlaylists()
    }

    override fun copyPlaylistCoverToLocalStorage(uri: String): String {
        return newPlaylistRepository.copyPlaylistCoverToLocalStorage(uri)
    }
}