package com.example.playlistmaker.ui.search

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track) {
        with(binding) {
            track.text = item.trackName
            artist.text = item.artistName
            duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
            Glide.with(albumCover.context)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(dpToPx(2f, albumCover.context)))
                .into(albumCover)
            artist.requestLayout()
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}