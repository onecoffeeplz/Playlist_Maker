package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.AppThemeInteractor
import com.example.playlistmaker.settings.domain.impl.AppThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.api.ActionHandlerInteractor
import com.example.playlistmaker.sharing.domain.impl.ActionHandlerInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<AppThemeInteractor> {
        AppThemeInteractorImpl(theme = get())
    }

    factory<ActionHandlerInteractor> {
        ActionHandlerInteractorImpl(action = get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(player = get())
    }

}