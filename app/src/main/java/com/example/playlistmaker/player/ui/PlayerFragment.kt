package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment(), BottomSheetAdapter.OnPlaylistClickListener {
    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentPlayerBinding must not be null!")

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var playButton: ImageButton
    private lateinit var url: String
    private lateinit var track: Track

    private val playlists: MutableList<Playlist> = mutableListOf()
    private val playlistsAdapter = BottomSheetAdapter(playlists, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = binding.playBtn

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isFitToContents = false
            halfExpandedRatio = 0.66f
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.addBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            binding.listenProgress.text = state.progressText
            playButton.isEnabled = state !is PlayerScreenState.Default
            setFavoriteButton(state.isTrackFavorite)

            playlists.clear()
            playlists.addAll(state.allPlaylists)
            playlistsAdapter.notifyDataSetChanged()

            when (state) {
                is PlayerScreenState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_pause)
                }

                is PlayerScreenState.Default, is PlayerScreenState.Prepared, is PlayerScreenState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_play)
                }

                else -> {
                    playButton.setImageResource(R.drawable.ic_play)
                    Toast.makeText(requireContext(), getString(R.string.smth_wrong), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.onTrackAddTrigger().observe(viewLifecycleOwner) { (isAdded, playlistName) ->
            if (isAdded) {
                Toast.makeText(requireContext(), getString(R.string.track_added_to_playlist, playlistName), Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(requireContext(), getString(R.string.track_was_in_playlist, playlistName), Toast.LENGTH_SHORT).show()
            }
        }

        binding.mediaToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val jsonTrack = arguments?.getString("track")
        track = Gson().fromJson(jsonTrack, Track::class.java)
        url = track.previewUrl
        viewModel.preparePlayer(url, track)

        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            year.text = track.releaseDate.substring(0, 4)
            country.text = track.country
            if (track.collectionName.isNullOrEmpty()) {
                albumDetailsGroup.visibility = View.GONE
            } else {
                album.text = track.collectionName
            }
            genre.text = track.primaryGenreName
            duration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(albumCover.context)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(dpToPx(8f, albumCover.context)))
                .into(albumCover)
        }

        playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoritesBtn.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylists.adapter = playlistsAdapter

        binding.createPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun setFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoritesBtn.setImageResource(R.drawable.fab_favorites_enable)
        } else {
            binding.favoritesBtn.setImageResource(R.drawable.fab_favorites)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        playButton.setImageResource(R.drawable.ic_play)
        viewModel.onPausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onRelease()
        _binding = null
    }

    override fun onPlaylistClick(playlist: Playlist) {
        viewModel.onAddToPlaylistClick(track, playlist)
    }

}