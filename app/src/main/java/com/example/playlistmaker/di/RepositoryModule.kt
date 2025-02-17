package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.impl.AppThemeRepositoryImpl
import com.example.playlistmaker.settings.data.local.LocalDataSourceImpl
import com.example.playlistmaker.settings.domain.api.AppThemeRepository
import com.example.playlistmaker.settings.domain.api.LocalDataSource
import com.example.playlistmaker.sharing.data.impl.ActionHandlerRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.ActionHandlerRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<LocalDataSource> {
        LocalDataSourceImpl(sharedPreferences = get())
    }

    factory<AppThemeRepository> {
        AppThemeRepositoryImpl(storage = get(), context = get())
    }

    factory<ActionHandlerRepository> {
        ActionHandlerRepositoryImpl(context = get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(storage = get(), gson = get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

}