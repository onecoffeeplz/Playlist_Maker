package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
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
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "pm_database.db").build()
    }

}



