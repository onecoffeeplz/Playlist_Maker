package com.example.playlistmaker

import android.app.Application
import android.app.Application.MODE_PRIVATE
import com.example.playlistmaker.data.ActionHandlerRepositoryImpl
import com.example.playlistmaker.data.AppThemeRepositoryImpl
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.local.LocalRepositoryImpl
import com.example.playlistmaker.data.local.LocalRepositoryImpl.Companion.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.data.network.RetrofitClient
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


object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    fun provideLocalRepository(): LocalRepository {
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

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideLocalRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}