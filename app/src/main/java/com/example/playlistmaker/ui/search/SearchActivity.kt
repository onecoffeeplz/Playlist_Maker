package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.network.SearchHistory
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.main.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {

    private var _binding: ActivitySearchBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySearchBinding must not be null!")

    private var userInput: String = ""
    private var cursorPosition: Int = 0

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { doSearch(userInput) }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    private val trackList: MutableList<Track> = mutableListOf()
    private lateinit var searchHistoryTrackList: MutableList<Track>
    private val trackAdapter = TrackAdapter(trackList, this)
    private var searchAdapter = TrackAdapter(trackList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryTrackList = searchHistory.getSearchHistory()
        searchAdapter = TrackAdapter(searchHistoryTrackList, this)

        with (binding.searchbar) {
            postDelayed({setKeyboardAndCursor(this)}, 100)
            setOnFocusChangeListener { _, hasFocus ->
                binding.searchHistory.visibility =
                    if (hasFocus && searchHistoryTrackList.isNotEmpty() && binding.searchbar.text.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.searchToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.clearButton.setOnClickListener {
            binding.searchbar.text.clear()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            binding.searchNetworkError.visibility = View.GONE
            binding.searchNothingFound.visibility = View.GONE
            setKeyboardAndCursor(binding.searchbar)
        }

        binding.clearHistoryButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            searchHistoryTrackList.clear()
            searchAdapter.notifyDataSetChanged()
            binding.searchHistory.visibility = View.GONE
            setKeyboardAndCursor(binding.searchbar)
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearButton.visibility = View.GONE
                    binding.searchHistory.visibility =
                        if (binding.searchbar.hasFocus() && searchHistoryTrackList.isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    userInput = s.toString()
                    binding.searchHistory.visibility = View.GONE
                    binding.clearButton.visibility = View.VISIBLE
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchbar.addTextChangedListener(textWatcher)

        with(binding.rvTracks) {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = trackAdapter
        }

        with(binding.rvHistoryTracks) {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = searchAdapter
        }

        binding.reloadButton.setOnClickListener {
            doSearch(userInput)
        }
        binding.searchbar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch(userInput)
            }
            return@setOnEditorActionListener true
        }
    }

    private fun hideKeyboardAndCursor() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View? = currentFocus
        if (view is EditText) view.clearFocus()
        if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setKeyboardAndCursor(view: View) {
        view.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun doSearch(userInput: String) {
        if (userInput.isNotEmpty()) {
            binding.rvTracks.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            RetrofitClient.iTunesService.search(userInput)
                .enqueue(object : Callback<TracksSearchResponse> {
                    override fun onResponse(
                        call: Call<TracksSearchResponse>,
                        response: Response<TracksSearchResponse>
                    ) {
                        binding.searchNetworkError.visibility = View.GONE
                        binding.searchNothingFound.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                binding.rvTracks.visibility = View.VISIBLE
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                binding.searchNothingFound.visibility = View.VISIBLE
                                binding.searchNetworkError.visibility = View.GONE
                            }
                        } else {
                            binding.searchNothingFound.visibility = View.GONE
                            binding.searchNetworkError.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                        hideKeyboardAndCursor()
                        binding.progressBar.visibility = View.GONE
                        binding.searchNothingFound.visibility = View.GONE
                        binding.searchNetworkError.visibility = View.VISIBLE
                    }
                })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, userInput)
        outState.putInt(CURSOR_POSITION, binding.searchbar.selectionStart)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchbar.setText(savedInstanceState.getString(SEARCH_INPUT, userInput))
        binding.searchbar.setSelection(savedInstanceState.getInt(CURSOR_POSITION, cursorPosition))
    }

    override fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", Gson().toJson(track))
            }
            startActivity(intent)

            searchHistory.addTrack(track)
            searchHistory.checkConditionsAndAdd(track, searchHistoryTrackList)
            searchAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun clickDebounce() : Boolean {
        val currentClickState = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return currentClickState
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val CURSOR_POSITION = "CURSOR_POSITION"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }
}