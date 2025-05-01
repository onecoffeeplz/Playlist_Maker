package com.example.playlistmaker.media.domain.models

data class Playlist (
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistCoverUri: String?,
    val tracksIds: String?,
    val tracksCount: Int = 0,
)