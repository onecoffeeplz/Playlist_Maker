package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest

class RetrofitClient(private val iTunesService: ITunesAPI) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body() ?: Response()
            return body.apply { responseCode = response.code() }
        } else {
            return Response().apply { responseCode = 400 }
        }
    }

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
    }
}