package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.presentation.SearchState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson

class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {

    private var _binding: ActivitySearchBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySearchBinding must not be null!")

    private lateinit var viewModel: SearchViewModel
    private val trackList: MutableList<Track> = mutableListOf()
    private val searchAdapter = TrackAdapter(trackList, this)
    private var historyAdapter = TrackAdapter(trackList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]
        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showSearchResults(state.tracks)
                is SearchState.Error -> showError(state.error)
                is SearchState.NothingFound -> showNothingFound()
                is SearchState.History -> showHistory(state.tracks)
            }
        }

        with(binding.rvTracks) {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = searchAdapter
        }

        with(binding.rvHistoryTracks) {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = historyAdapter
        }

        with(binding) {
            searchToolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            clearButton.setOnClickListener {
                viewModel.onClearButtonClick()
                searchbar.text.clear()
                setKeyboardAndCursor(searchbar)
            }

            clearHistoryButton.setOnClickListener {
                viewModel.onClearHistoryButtonClick()
                hideAll()
                setKeyboardAndCursor(searchbar)
            }

            reloadButton.setOnClickListener {
                viewModel.researchDebounce()
            }
        }

        with(binding.searchbar) {
            postDelayed({ setKeyboardAndCursor(this) }, 100)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) updateHistoryView(historyAdapter.trackList)
                binding.searchHistory.visibility =
                    if (hasFocus && binding.searchbar.text.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.searchbar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(binding.searchbar.text.toString())
            }
            return@setOnEditorActionListener true
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearButton.visibility = View.GONE
                    updateHistoryView(historyAdapter.trackList)
                    binding.rvTracks.visibility = View.GONE
                } else {
                    binding.rvTracks.visibility = View.VISIBLE
                    binding.searchHistory.visibility = View.GONE
                    binding.clearButton.visibility = View.VISIBLE
                }
                viewModel.searchDebounce(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchbar.addTextChangedListener(textWatcher)
    }

    private fun hideAll() {
        with(binding) {
            progressBar.visibility = View.GONE
            searchHistory.visibility = View.GONE
            searchNetworkError.visibility = View.GONE
            searchNothingFound.visibility = View.GONE
            rvTracks.visibility = View.GONE
        }
        hideKeyboardAndCursor()
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

    private fun showLoading() {
        hideAll()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError(error: ErrorType) {
        hideAll()
        binding.searchNetworkError.visibility = View.VISIBLE
    }

    private fun showNothingFound() {
        hideAll()
        binding.searchNothingFound.visibility = View.VISIBLE
    }

    private fun showSearchResults(tracks: List<Track>) {
        hideAll()
        searchAdapter.trackList = tracks as MutableList<Track>
        binding.rvTracks.visibility = View.VISIBLE
        binding.clearButton.visibility = View.VISIBLE
        searchAdapter.notifyDataSetChanged()
    }

    private fun showHistory(tracks: List<Track>) {
        hideAll()
        historyAdapter.trackList = tracks as MutableList<Track>
        historyAdapter.notifyDataSetChanged()
        setKeyboardAndCursor(binding.searchbar)
        updateHistoryView(tracks)
    }

    private fun updateHistoryView(tracks: List<Track>) {
        with(binding) {
            searchHistory.visibility =
                if (searchbar.text.isNullOrEmpty() && searchbar.hasFocus() && tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onTrackClick(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.onClick(track)
            historyAdapter.notifyDataSetChanged()
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", Gson().toJson(track))
            }
            startActivity(intent)
        }
    }

}