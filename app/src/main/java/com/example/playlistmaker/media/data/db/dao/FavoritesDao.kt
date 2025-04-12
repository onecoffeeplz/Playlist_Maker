package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.FavoritesEntity

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: FavoritesEntity)

    @Delete
    suspend fun removeFromFavorites(track: FavoritesEntity)

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    suspend fun getFavorites(): List<FavoritesEntity>

    @Query("SELECT COUNT(*) from favorites WHERE trackId = :trackId")
    suspend fun isInFavorites(trackId: Int): Int

}