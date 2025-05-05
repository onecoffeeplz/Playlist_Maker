package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.network.ITunesAPI
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.data.network.RetrofitClient.Companion.BASE_URL
import com.example.playlistmaker.settings.data.local.LocalDataSourceImpl.Companion.PLAYLIST_MAKER_PREFERENCES
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    single { Gson() }

    single<ITunesAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitClient(iTunesService = get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "pm_database.db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            ).build()
    }

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE playlists (playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, playlistName TEXT NOT NULL, playlistDescription TEXT, playlistCoverUri TEXT, tracksIds TEXT, tracksCount INTEGER NOT NULL DEFAULT 0)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tracks (
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL
            )
        """.trimIndent()
        )
    }
}