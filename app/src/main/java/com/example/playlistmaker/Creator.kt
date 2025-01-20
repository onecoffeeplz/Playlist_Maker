package com.example.playlistmaker

import android.app.Application
import android.app.Application.MODE_PRIVATE
import android.media.MediaPlayer
import com.example.playlistmaker.data.ActionHandlerRepositoryImpl
import com.example.playlistmaker.data.AppThemeRepositoryImpl
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.local.LocalRepositoryImpl
import com.example.playlistmaker.data.local.LocalRepositoryImpl.Companion.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.data.network.ITunesAPI
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.network.RetrofitClient.Companion.BASE_URL
import com.example.playlistmaker.domain.api.ActionHandlerInteractor
import com.example.playlistmaker.domain.api.ActionHandlerRepository
import com.example.playlistmaker.domain.api.AppThemeInteractor
import com.example.playlistmaker.domain.api.AppThemeRepository
import com.example.playlistmaker.domain.api.LocalRepository
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.ActionHandlerInteractorImpl
import com.example.playlistmaker.domain.impl.AppThemeInteractorImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun provideLocalRepository(): LocalRepository {
        return LocalRepositoryImpl(
            application.getSharedPreferences(
                PLAYLIST_MAKER_PREFERENCES,
                MODE_PRIVATE
            )
        )
    }

    private fun getAppThemeRepository(): AppThemeRepository {
        return AppThemeRepositoryImpl(provideLocalRepository(), application)
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
        return SearchHistoryRepositoryImpl(provideLocalRepository(), getGson())
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