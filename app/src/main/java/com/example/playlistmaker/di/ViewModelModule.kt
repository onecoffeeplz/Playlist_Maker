package com.example.playlistmaker.di

import com.example.playlistmaker.media.presentation.FavoritesViewModel
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<PlayerViewModel> {
        PlayerViewModel(interactor = get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(tracksInteractor = get(), searchHistoryInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(appThemeInteractor = get(), actionHandler = get())
    }

    viewModel<FavoritesViewModel> {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel()
    }

}