package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.presentation.FavoritesState
import com.example.playlistmaker.media.presentation.FavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import dev.androidbroadcast.vbpd.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(), FavoritesAdapter.OnTrackClickListener {

    private val binding by viewBinding(FragmentFavoritesBinding::bind)

    private val viewModel by viewModel<FavoritesViewModel>()
    private val trackList: MutableList<Track> = mutableListOf()
    private val favoritesAdapter = FavoritesAdapter(trackList, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFavoritesBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Loading -> showLoading()
                is FavoritesState.Content -> showFavorites(state.tracks)
                is FavoritesState.Empty -> showEmpty()
            }
        }

        viewModel.onTrackClickTrigger().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }

        with(binding.rvFavoritesTracks) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter
        }

        viewModel.getTracksFromFavorites()
    }

    private fun hideAll() {
        with(binding) {
            progressBar.visibility = View.GONE
            favoritesEmpty.visibility = View.GONE
            rvFavoritesTracks.visibility = View.GONE
        }
    }

    private fun showLoading() {
        hideAll()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        hideAll()
        binding.favoritesEmpty.visibility = View.VISIBLE
    }

    private fun showFavorites(tracks: List<Track>) {
        hideAll()
        favoritesAdapter.trackList = tracks as MutableList<Track>
        binding.rvFavoritesTracks.visibility = View.VISIBLE
        favoritesAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putString("track", Gson().toJson(track))
        }
        val playerFragment = PlayerFragment()
        playerFragment.arguments = bundle
        findNavController().navigate(R.id.action_mediaFragment_to_playerFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTracksFromFavorites()
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    override fun onTrackClick(track: Track) {
        viewModel.clickDebounce(track)
        favoritesAdapter.notifyDataSetChanged()
    }
}