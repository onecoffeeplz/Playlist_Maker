package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    fun copyPlaylistCoverToLocalStorage(uri: String): String
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistDetails(playlistId: Int): Pair<Playlist, List<Track>>
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int)
    fun sharePlaylist(playlist: Playlist, tracks: List<Track>)
    suspend fun deletePlaylist(playlist: Playlist)
}