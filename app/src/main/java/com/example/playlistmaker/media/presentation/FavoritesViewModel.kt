package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoritesInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SingleEventLiveData
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var isClickAllowed = true
    private val favoritesState = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = favoritesState

    private val _onTrackClickTrigger = SingleEventLiveData<Track>()
    fun onTrackClickTrigger(): LiveData<Track> = _onTrackClickTrigger

    fun getTracksFromFavorites() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect {
                tracks -> processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        favoritesState.postValue(state)
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            _onTrackClickTrigger.value = track
            trackClickDebounce(Unit)
        }
    }

    private val trackClickDebounce =
        debounce<Unit>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = true
        }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}