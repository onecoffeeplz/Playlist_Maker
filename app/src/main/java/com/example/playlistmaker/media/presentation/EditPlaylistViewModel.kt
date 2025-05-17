package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPlaylistViewModel(
    val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor
) :
    NewPlaylistViewModel(playlistInteractor) {

    private val _playlistInfoLiveData = MutableLiveData<Playlist>()
    fun getPlaylistInfo(): LiveData<Playlist> = _playlistInfoLiveData

    private lateinit var playlist: Playlist
    private var tracks: List<Track> = emptyList()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val pair = playlistInteractor.getPlaylistDetails(playlistId)
                pair
            }.let { pair ->
                _playlistInfoLiveData.postValue(pair.first)
                playlist = pair.first
                tracks = pair.second
            }
        }
    }

    override fun createPlaylist(name: String, description: String, coverUri: String?) {
        val localCoverUri: String? = if (coverUri != null && coverUri != playlist.playlistCoverUri) {
            playlistInteractor.copyPlaylistCoverToLocalStorage(coverUri)
        } else {
            playlist.playlistCoverUri
        }
        val editedPlaylist = Playlist(
            playlistId = playlistId,
            playlistName = name,
            playlistDescription = description,
            playlistCoverUri = localCoverUri,
            tracksIds = tracks.joinToString(",") { it.trackId.toString() },
            tracksCount = tracks.size
        )

        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(editedPlaylist)
        }
    }
}