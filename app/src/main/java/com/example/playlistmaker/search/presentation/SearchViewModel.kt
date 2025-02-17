package com.example.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchState = MutableLiveData<SearchState>()
    private var userInput: String = ""

    private val _onTrackClickTrigger = SingleEventLiveData<Track>()
    fun onTrackClickTrigger(): LiveData<Track> = _onTrackClickTrigger

    private val searchRunnable = Runnable {
        if (userInput.isNotEmpty()) {
            doSearch(userInput)
        }
    }

    fun observeState(): LiveData<SearchState> = searchState

    private val trackList: MutableList<Track> = mutableListOf()
    private val searchHistoryTrackList: MutableList<Track> = mutableListOf()

    init {
        searchHistoryTrackList.addAll(searchHistoryInteractor.getSearchHistory())
        searchState.postValue(SearchState.History(searchHistoryTrackList))
    }

    private fun doSearch(userInput: String) {
        searchState.postValue(SearchState.Loading)

        tracksInteractor.search(userInput, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                handler.post {
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
            }
        })
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            addTrackToHistory(track)
            _onTrackClickTrigger.value = track
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
    }

    fun searchDebounce(changedText: String) {
        if (userInput == changedText) return
        userInput = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun researchDebounce() {
        if (userInput.isNotEmpty()) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}