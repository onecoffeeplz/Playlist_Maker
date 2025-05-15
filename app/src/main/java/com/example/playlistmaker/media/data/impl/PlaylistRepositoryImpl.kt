package com.example.playlistmaker.media.data.impl

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        try {
            val playlistEntity = playlistDbConverter.map(playlist)
            appDatabase.playlistDao().createPlaylist(playlistEntity)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> =
        appDatabase.playlistDao().getPlaylists().map { convertFromPlaylistEntity(it) }

    override fun copyPlaylistCoverToLocalStorage(uri: String): String {
        val originalFileName = getFileNameFromUri(Uri.parse(uri), context)

        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLISTS_COVER_DIR
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val timestampString = System.currentTimeMillis().toString()
        val file = File(filePath, originalFileName ?: "$timestampString.jpg")

        val inputStream = context.contentResolver.openInputStream(uri.toUri())
        val outputStream = FileOutputStream(file)

        try {
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        } finally {
            inputStream?.close()
            outputStream.close()
        }

        return file.toUri().toString()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val trackEntity = trackDbConverter.map(track)
        val playlistEntity = playlistDbConverter.map(playlist)

        appDatabase.playlistDao().addTrack(trackEntity)

        val trackIdsList = playlistEntity.tracksIds?.split(",")?.map { it.trim() } ?: emptyList()
        val trackInList = trackIdsList.contains(trackEntity.trackId.toString())

        if (trackInList) {
            return false
        } else {
            val updatedTracksIds = if (playlistEntity.tracksIds.isNullOrEmpty()) {
                trackEntity.trackId.toString()
            } else {
                "${playlistEntity.tracksIds},${trackEntity.trackId}"
            }

            playlistEntity.tracksIds = updatedTracksIds
            playlistEntity.tracksCount = countTracks(updatedTracksIds)
            appDatabase.playlistDao().updatePlaylist(playlistEntity)
            return true
        }
    }

    override suspend fun getPlaylistDetails(playlistId: Int): Pair<Playlist, List<Track>> {
        val playlistEntity = appDatabase.playlistDao().getPlaylistDetails(playlistId)
        val playlist = playlistDbConverter.map(playlistEntity)

        val trackIdsString = playlistEntity.tracksIds ?: ""
        val trackIds = trackIdsString.split(",").mapNotNull { it.trim().toIntOrNull() }

        val tracks = trackIds.mapNotNull { trackId ->
            val trackEntity = appDatabase.playlistDao().getTrackById(trackId)
            trackDbConverter.map(trackEntity)
        }.reversed()

        return Pair(playlist, tracks)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val playlist = appDatabase.playlistDao().getPlaylistDetails(playlistId)
        val tracksList =
            playlist.tracksIds?.split(",")?.map { it.trim() }?.toMutableList() ?: mutableListOf()
        val removed = tracksList.remove(trackId.toString())
        val newTracksIds = if (tracksList.isEmpty()) null else tracksList.joinToString(",")
        val updatedPlaylist = playlist.copy(
            tracksIds = newTracksIds,
            tracksCount = (playlist.tracksCount - if (removed) 1 else 0).coerceAtLeast(0)
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)

        val trackIsInOtherPlaylists = isTrackInAnyPlaylist(trackId)
        if (!trackIsInOtherPlaylists) {
            appDatabase.playlistDao().deleteTrackById(trackId)
        }
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val message = prepareMessage(playlist, tracks)
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(share)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)

        val tracksList = playlist.tracksIds?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()

        val allPlaylists = appDatabase.playlistDao().getPlaylists().first()
        val trackIdsInPlaylists = allPlaylists
            .flatMap { it -> it.tracksIds?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList() }
            .toSet()

        for (trackId in tracksList) {
            if (!trackIdsInPlaylists.contains(trackId)) {
                appDatabase.playlistDao().deleteTrackById(trackId)
            }
        }
    }

    private fun prepareMessage(playlist: Playlist, tracks: List<Track>): String {
        val description =
            if (!playlist.playlistDescription.isNullOrEmpty()) "\nОписание: ${playlist.playlistDescription}" else ""
        val trackList = tracks.joinToString("\n") { track ->
            "${tracks.indexOf(track) + 1}. ${track.artistName} - ${track.trackName} (${
                formatDuration(
                    track.trackTimeMillis
                )
            })"
        }
        return "**${playlist.playlistName}**$description\nКоличество треков: ${playlist.tracksCount}\n\nСписок треков:\n$trackList"
    }

    private fun formatDuration(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    private suspend fun isTrackInAnyPlaylist(trackId: Int): Boolean {
        val allPlaylists = appDatabase.playlistDao().getPlaylists().first()

        return allPlaylists.any { playlist ->
            playlist.tracksIds?.split(",")?.map { it.trim() }?.contains(trackId.toString()) == true
        }
    }

    private fun countTracks(tracksIds: String): Int {
        val ids = tracksIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return ids.size
    }

    private fun getFileNameFromUri(uri: Uri, context: Context): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
        return fileName
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    companion object {
        private const val PLAYLISTS_COVER_DIR = "playlists_covers"
    }
}