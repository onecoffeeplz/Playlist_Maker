package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        return playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun copyPlaylistCoverToLocalStorage(uri: String): String {
        return playlistRepository.copyPlaylistCoverToLocalStorage(uri)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistDetails(playlistId: Int): Pair<Playlist, List<Track>> {
        return playlistRepository.getPlaylistDetails(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        return playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }
}