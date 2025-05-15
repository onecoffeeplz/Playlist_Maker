package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private lateinit var viewModel: EditPlaylistViewModel
    private val playlistId by lazy {
        arguments?.getInt("playlistId")
            ?: throw IllegalArgumentException("playlistId must be passed as an argument!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(playlistId) }
        viewModel.getPlaylistInfo().observe(viewLifecycleOwner) { playlist ->
            with(binding) {
                playlistName.setText(playlist.playlistName)
                playlistDescription.setText(playlist.playlistDescription)
                if (playlist.playlistCoverUri != null) {
                    playlistCover.setImageURI(playlist.playlistCoverUri.toUri())
                } else {
                    playlistCover.setImageResource(R.drawable.ic_placeholder)
                }
            }
        }

        backPressedCallback.remove()

        with(binding) {
            newPlaylistToolbar.title = getString(R.string.edit_playlist)
            newPlaylistToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            playlistCreate.text = getString(R.string.save)
            playlistCreate.setOnClickListener{
                viewModel.createPlaylist(
                    binding.playlistName.text.toString(),
                    binding.playlistDescription.text.toString(),
                    uri?.toString()
                )
                findNavController().navigateUp()
            }
        }
    }

    override fun handleBackPressed() {
        findNavController().navigateUp()
    }
}