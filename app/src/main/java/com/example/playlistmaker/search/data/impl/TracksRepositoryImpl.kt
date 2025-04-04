package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        try {
            when (response.responseCode) {
                200 -> {
                    emit(Resource.Success((response as TracksSearchResponse).results.map {
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
                    }))
                }

                404 -> {
                    emit(Resource.Error(ErrorType.NOTHING_FOUND))
                }

                500 -> {
                    emit(Resource.Error(ErrorType.SERVER_ERROR))
                }

                else -> {
                    emit(Resource.Error(ErrorType.NO_CONNECTION))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(ErrorType.NO_CONNECTION))
        }
    }
}