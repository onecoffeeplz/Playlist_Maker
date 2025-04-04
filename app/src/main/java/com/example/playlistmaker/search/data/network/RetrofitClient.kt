package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClient(private val iTunesService: ITunesAPI) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { responseCode = 400 }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.search(dto.expression)
                return@withContext if (response.results.isEmpty()) {
                    Response().apply { responseCode = 404 }
                } else {
                    response.apply { responseCode = 200 }
                }
            } catch (e: Throwable) {
                Response().apply { responseCode = 500 }
            }
        }
    }

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
    }
}