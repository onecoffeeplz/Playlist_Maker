package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search(expression: String): Flow<Resource<List<Track>>>
}