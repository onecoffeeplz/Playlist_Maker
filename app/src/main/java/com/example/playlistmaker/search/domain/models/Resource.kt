package com.example.playlistmaker.search.domain.models

sealed class Resource<T>(val data: List<Track>? = null, val errorType: ErrorType? = null) {
    class Success<T>(data: List<Track>): Resource<T>(data)
    class Error<T>(errorType: ErrorType, data: List<Track>? = null): Resource<T>(data, errorType)
}