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
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistDetailsState
import com.example.playlistmaker.media.presentation.PlaylistDetailsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentPlaylistsDetailsBinding must not be null!")

    private val playlistId by lazy { arguments?.getInt("playlistId") ?: throw IllegalArgumentException("playlistId must be passed as an argument!") }
    private lateinit var tracks: List<Track>

    private lateinit var viewModel: PlaylistDetailsViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
                val desiredHeight = screenHeight - (shareIconBottom + resources.getDimension(R.dimen.padding_8).toInt())
                bottomSheetBehavior.peekHeight = desiredHeight
                shareIcon.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        shareIcon.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    private fun showContent(playlistInfo: Playlist, tracks: List<Track>) {
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

            val trackCount = tracks.size
            val trackText = localizedContext.resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)
            playlistTracksCount.text = trackText

            val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
            val totalDurationMinutes = (totalDurationMillis / (60 * 1000)).toInt()
            val totalDurationText = localizedContext.resources.getQuantityString(R.plurals.minutes_count, totalDurationMinutes, totalDurationMinutes)
            playlistDuration.text = totalDurationText

            overlay.visibility = View.GONE
            playlistBottomSheet.visibility = View.VISIBLE
            playlistMoreBottomSheet.visibility = View.GONE
            playlist.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            overlay.visibility = View.GONE
            playlistBottomSheet.visibility = View.GONE
            playlistMoreBottomSheet.visibility = View.GONE
            playlist.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
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

}