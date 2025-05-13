package com.example.playlistmaker.media.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistDetailsState
import com.example.playlistmaker.media.presentation.PlaylistDetailsViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlaylistDetailsFragment : Fragment(), PlaylistDetailsTrackAdapter.OnTrackClickListener,
    PlaylistDetailsTrackAdapter.OnTrackLongClickListener {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentPlaylistsDetailsBinding must not be null!")

    private val playlistId by lazy {
        arguments?.getInt("playlistId")
            ?: throw IllegalArgumentException("playlistId must be passed as an argument!")
    }

    private lateinit var viewModel: PlaylistDetailsViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var playlist: Playlist
    private var tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = PlaylistDetailsTrackAdapter(tracks, this, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(playlistId) }

        binding.mediaToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvPlaylistTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylistTracks.adapter = tracksAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPlaylistDetails(playlistId)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailsState.Loading -> showLoading()
                is PlaylistDetailsState.Content -> showContent(state.playlist, state.tracks)
            }
        }

        val bottomSheet: ConstraintLayout = binding.playlistBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val shareIcon: ImageView = binding.playlistShare
        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val shareIconHeight = shareIcon.height
                val shareIconLocation = IntArray(2)
                shareIcon.getLocationOnScreen(shareIconLocation)
                val shareIconBottom = shareIconLocation[1] + shareIconHeight
                val desiredHeight =
                    screenHeight - (shareIconBottom + resources.getDimension(R.dimen.padding_8)
                        .toInt())
                bottomSheetBehavior.peekHeight = desiredHeight
                shareIcon.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        shareIcon.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        val moreBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistMoreBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        moreBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        with(binding) {
                            overlay.visibility = View.VISIBLE
                            playlist.playlistCoverUri?.let { uri ->
                                playlistInfo.playlistCover.setImageURI(uri.toUri())
                            } ?: run {
                                playlistInfo.playlistCover.setImageResource(R.drawable.ic_placeholder)
                            }
                            playlistInfo.playlistName.text = playlist.playlistName
                            playlistInfo.playlistTracksCount.text =
                                getLocalizedTrackCountText(playlist.tracksCount)
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.playlistMore.setOnClickListener {
            moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistShare.setOnClickListener { sharePlaylist() }
        binding.playlistMoreShare.setOnClickListener { sharePlaylist() }

        binding.playlistMoreDelete.setOnClickListener { showDeletePlaylistDialog(playlist) }

    }

    private fun sharePlaylist() {
        if (tracks.size > 0) {
            viewModel.sharePlaylist(playlist, tracks)
        } else {
            showEmptyPlaylistMessage()
        }
    }

    private fun showContent(playlistInfo: Playlist, playlistTracks: List<Track>) {
        tracksAdapter.trackList = playlistTracks as MutableList<Track>
        playlist = playlistInfo
        with(binding) {
            playlistName.text = playlistInfo.playlistName
            if (playlistInfo.playlistDescription.isNullOrEmpty()) {
                playlistDescription.visibility = View.GONE
            } else {
                playlistDescription.text = playlistInfo.playlistDescription
            }
            if (playlistInfo.playlistCoverUri.isNullOrEmpty()) {
                playlistCover.setImageResource(R.drawable.ic_placeholder)
            } else {
                playlistCover.setImageURI(playlistInfo.playlistCoverUri.toUri())
            }

            val localizedContext = setLocale(requireContext(), "ru")

            playlistTracksCount.text = getLocalizedTrackCountText(playlistTracks.size)

            val totalDurationMillis = playlistTracks.sumOf { it.trackTimeMillis }
            val totalDurationMinutes = (totalDurationMillis / (60 * 1000)).toInt()
            val totalDurationText = localizedContext.resources.getQuantityString(
                R.plurals.minutes_count,
                totalDurationMinutes,
                totalDurationMinutes
            )
            playlistDuration.text = totalDurationText

            overlay.visibility = View.GONE
            playlistBottomSheet.visibility = View.VISIBLE
            playlistMoreBottomSheet.visibility = View.VISIBLE
            playlistDetails.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        tracks.clear()
        tracks.addAll(playlistTracks)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun getLocalizedTrackCountText(trackCount: Int): String {
        val localizedContext = setLocale(requireContext(), "ru")
        val trackText = localizedContext.resources.getQuantityString(
            R.plurals.tracks_count,
            trackCount,
            trackCount
        )
        return trackText
    }

    private fun showLoading() {
        with(binding) {
            overlay.visibility = View.GONE
            playlistBottomSheet.visibility = View.GONE
            playlistMoreBottomSheet.visibility = View.GONE
            playlistDetails.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun setLocale(context: Context, language: String = "ru"): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(playlistId: Int) = PlaylistDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt("playlistId", playlistId)
            }
        }
    }

    override fun onTrackClick(track: Track) {
        val bundle = Bundle().apply {
            putString("track", Gson().toJson(track))
        }
        val playerFragment = PlayerFragment()
        playerFragment.arguments = bundle
        findNavController().navigate(R.id.action_playlistDetailsFragment_to_playerFragment, bundle)
    }

    override fun onTrackLongClick(track: Track): Boolean {
        showDeleteTrackDialog(track.trackId)
        return true
    }

    private fun showDeleteTrackDialog(trackId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.playlist_details_dialog_body)
            .setNegativeButton(R.string.playlist_details_dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.playlist_details_dialog_yes) { dialog, _ ->
                viewModel.removeTrackFromPlaylist(playlistId, trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun showEmptyPlaylistMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.playlist_details_empty_playlist)
            .setPositiveButton(R.string.playlist_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.playlist_details_delete_playlist, playlist.playlistName))
            .setNegativeButton(R.string.playlist_details_dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.playlist_details_dialog_yes) { dialog, _ ->
                viewModel.deletePlaylist(playlist)
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

}