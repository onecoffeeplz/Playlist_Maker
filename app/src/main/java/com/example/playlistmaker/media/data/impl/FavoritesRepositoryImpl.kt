package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.converters.FavoritesDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.FavoritesEntity
import com.example.playlistmaker.media.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoritesDbConverter: FavoritesDbConverter,
) : FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        val favoritesEntity = favoritesDbConverter.map(track)
        appDatabase.favoritesDao().addToFavorites(favoritesEntity)
    }

    override suspend fun removeFromFavorites(track: Track) {
        val favoritesEntity = favoritesDbConverter.map(track)
        appDatabase.favoritesDao().removeFromFavorites(favoritesEntity)
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getFavorites()
        emit(convertFromFavoritesEntity(tracks))
    }

    private fun convertFromFavoritesEntity(tracks: List<FavoritesEntity>): List<Track> {
        return tracks.map { track -> favoritesDbConverter.map(track) }
    }

    override suspend fun isInFavorites(trackId: Int): Boolean {
        return appDatabase.favoritesDao().isInFavorites(trackId) > 0
    }

}