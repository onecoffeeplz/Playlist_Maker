package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): Resource<List<Track>> {
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            when (response.responseCode) {
                200 -> {
                    Resource.Success((response as TracksSearchResponse).results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    })
                }

                404 -> {
                    Resource.Error(ErrorType.NOTHING_FOUND)
                }

                500, 502, 503 -> {
                    Resource.Error(ErrorType.SERVER_ERROR)
                }

                else -> {
                    Resource.Error(ErrorType.NO_CONNECTION)
                }
            }
        } catch (e: Exception) {
            Resource.Error(ErrorType.NO_CONNECTION)
        }
    }
}