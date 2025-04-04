package com.example.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private var isClickAllowed = true
    private val searchState = MutableLiveData<SearchState>()
    private var userInput: String = ""

    private val _onTrackClickTrigger = SingleEventLiveData<Track>()
    fun onTrackClickTrigger(): LiveData<Track> = _onTrackClickTrigger

    fun observeState(): LiveData<SearchState> = searchState

    private val trackList: MutableList<Track> = mutableListOf()
    private val searchHistoryTrackList: MutableList<Track> = mutableListOf()

    init {
        searchHistoryTrackList.addAll(searchHistoryInteractor.getSearchHistory())
        searchState.postValue(SearchState.History(searchHistoryTrackList))
    }

    private fun doSearch(userInput: String) {
        searchState.postValue(SearchState.Loading)

        viewModelScope.launch {
            tracksInteractor.search(userInput)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorType: ErrorType?) {
        when (errorType) {
            ErrorType.NO_CONNECTION, ErrorType.SERVER_ERROR -> searchState.postValue(
                SearchState.Error(errorType)
            )

            ErrorType.NOTHING_FOUND -> searchState.postValue(SearchState.NothingFound)
            null -> {
                val tracksResult = foundTracks ?: emptyList()
                if (tracksResult.isEmpty()) {
                    searchState.postValue(SearchState.NothingFound)
                } else {
                    trackList.clear()
                    trackList.addAll(tracksResult)
                    searchState.postValue(SearchState.Content(trackList))
                }
            }
        }
    }

    fun onClearButtonClick() {
        userInput = ""
        trackList.clear()
        searchState.postValue(SearchState.History(searchHistoryTrackList))
    }

    fun onClearHistoryButtonClick() {
        searchHistoryInteractor.clearSearchHistory()
        searchHistoryTrackList.clear()
        searchState.postValue(SearchState.History(searchHistoryTrackList))
    }

    private fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        searchHistoryInteractor.checkConditionsAndAdd(track, searchHistoryTrackList)
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            addTrackToHistory(track)
            _onTrackClickTrigger.value = track
            trackClickDebounce(Unit)
        }
    }

    private val trackClickDebounce =
        debounce<Unit>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = true
        }

    fun searchDebounce(changedText: String) {
        if (userInput == changedText) return
        userInput = changedText
        trackSearchDebounce(changedText)
    }

    fun researchDebounce() {
        if (userInput.isNotEmpty()) {
            trackSearchDebounce(userInput)
        }
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            doSearch(changedText)
        }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }
}