package com.example.playlistmaker.creator

import android.app.Application
import android.app.Application.MODE_PRIVATE
import android.media.MediaPlayer
import com.example.playlistmaker.sharing.data.impl.ActionHandlerRepositoryImpl
import com.example.playlistmaker.settings.data.impl.AppThemeRepositoryImpl
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.settings.data.local.LocalDataSourceImpl
import com.example.playlistmaker.settings.data.local.LocalDataSourceImpl.Companion.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.search.data.network.ITunesAPI
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.data.network.RetrofitClient.Companion.BASE_URL
import com.example.playlistmaker.sharing.domain.api.ActionHandlerInteractor
import com.example.playlistmaker.sharing.domain.api.ActionHandlerRepository
import com.example.playlistmaker.settings.domain.api.AppThemeInteractor
import com.example.playlistmaker.settings.domain.api.AppThemeRepository
import com.example.playlistmaker.settings.domain.api.LocalDataSource
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.sharing.domain.impl.ActionHandlerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.AppThemeInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    private fun provideLocalDataSource(): LocalDataSource {
        return LocalDataSourceImpl(
            application.getSharedPreferences(
                PLAYLIST_MAKER_PREFERENCES,
                MODE_PRIVATE
            )
        )
    }

    private fun getAppThemeRepository(): AppThemeRepository {
        return AppThemeRepositoryImpl(provideLocalDataSource(), application)
    }

    fun provideAppThemeInteractor(): AppThemeInteractor {
        return AppThemeInteractorImpl(getAppThemeRepository())
    }

    private fun getActionHandlerRepository(): ActionHandlerRepository {
        return ActionHandlerRepositoryImpl(application)
    }

    fun provideActionHandlerInteractor(): ActionHandlerInteractor {
        return ActionHandlerInteractorImpl(getActionHandlerRepository())
    }

    private fun getGson(): Gson {
        return Gson()
    }

    private fun getApiService(): ITunesAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val iTunesService: ITunesAPI by lazy {
            retrofit.create(ITunesAPI::class.java)
        }

        return iTunesService
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient(getApiService()))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideLocalDataSource(), getGson())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(getMediaPlayer())
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}