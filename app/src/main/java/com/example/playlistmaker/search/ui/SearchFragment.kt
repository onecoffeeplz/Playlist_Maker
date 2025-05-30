package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SearchState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson
import dev.androidbroadcast.vbpd.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), TrackAdapter.OnTrackClickListener {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel by viewModel<SearchViewModel>()
    private val trackList: MutableList<Track> = mutableListOf()
    private val searchAdapter = TrackAdapter(trackList, this)
    private val historyAdapter = TrackAdapter(trackList, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showSearchResults(state.tracks)
                is SearchState.Error -> showError(state.error)
                is SearchState.NothingFound -> showNothingFound()
                is SearchState.History -> showHistory(state.tracks)
            }
        }

        viewModel.onTrackClickTrigger().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }

        with(binding.rvTracks) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        with(binding.rvHistoryTracks) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        with(binding) {
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
                    viewModel.onClearButtonClick()
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
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View? = requireView().findFocus()
        if (view is EditText) view.clearFocus()
        if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setKeyboardAndCursor(view: View) {
        view.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        viewModel.clickDebounce(track)
        historyAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putString("track", Gson().toJson(track))
        }
        val playerFragment = PlayerFragment()
        playerFragment.arguments = bundle
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
    }

}