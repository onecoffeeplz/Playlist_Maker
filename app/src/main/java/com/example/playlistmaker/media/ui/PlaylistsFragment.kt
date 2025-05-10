package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistsState
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(), PlaylistsAdapter.OnPlaylistClickListener {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentPlaylistsBinding must not be null!")

    private val viewModel: PlaylistsViewModel by viewModel()
    private val playlists: MutableList<Playlist> = mutableListOf()
    private val playlistsAdapter = PlaylistsAdapter(playlists, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Empty -> showNothing()
                is PlaylistsState.Loading -> showLoading()
                is PlaylistsState.Content -> showContent(state.playlists)
            }
        }

        with(binding.rvPlaylists) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistsAdapter
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsAdapter.playlists = playlists as MutableList<Playlist>
        with(binding) {
            progressBar.visibility = View.GONE
            playlistEmpty.visibility = View.GONE
            rvPlaylists.visibility = View.VISIBLE
        }
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        with(binding) {
            playlistEmpty.visibility = View.GONE
            rvPlaylists.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun showNothing() {
        with(binding) {
            progressBar.visibility = View.GONE
            rvPlaylists.visibility = View.GONE
            playlistEmpty.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    override fun onPlaylistClick(playlist: Playlist) {
        val bundle = Bundle().apply {
            putInt("playlistId", playlist.playlistId)
        }
//        val playlistDetailsFragment = PlaylistDetailsFragment.newInstance(playlist.playlistId)
//        playlistDetailsFragment.arguments = bundle
        findNavController().navigate(R.id.playlistDetailsFragment, bundle)
    }
}