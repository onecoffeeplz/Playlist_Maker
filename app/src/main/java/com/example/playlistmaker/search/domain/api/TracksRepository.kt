package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun search(expression: String): Resource<List<Track>>
}