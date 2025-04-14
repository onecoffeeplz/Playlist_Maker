package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var _binding: ActivityPlayerBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityPlayerBinding must not be null!")

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var playButton: ImageButton
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.playBtn

        viewModel.observeState().observe(this) { state ->
            binding.listenProgress.text = state.progressText
            playButton.isEnabled = state !is PlayerScreenState.Default
            setFavoriteButton(state.isTrackFavorite)

            when (state) {
                is PlayerScreenState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_pause)
                }

                is PlayerScreenState.Default, is PlayerScreenState.Prepared, is PlayerScreenState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_play)
                }

                else -> {
                    playButton.setImageResource(R.drawable.ic_play)
                    Toast.makeText(this, getString(R.string.smth_wrong), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.mediaToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val jsonTrack = intent.getStringExtra("track")
        val track = Gson().fromJson(jsonTrack, Track::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onRelease()
    }

}