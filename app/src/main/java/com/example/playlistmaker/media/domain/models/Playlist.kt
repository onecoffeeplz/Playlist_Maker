package com.example.playlistmaker.media.domain.models

data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistCoverUri: String?,
    val tracksIds: String? = null,
    val tracksCount: Int = 0,
)