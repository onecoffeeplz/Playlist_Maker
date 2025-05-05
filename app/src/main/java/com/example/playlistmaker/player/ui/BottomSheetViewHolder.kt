package com.example.playlistmaker.player.ui

import android.content.Context
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemOnelineBinding
import com.example.playlistmaker.media.domain.models.Playlist

class BottomSheetViewHolder(private val binding: PlaylistItemOnelineBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val context: Context = itemView.context

    fun bind(item: Playlist) {
        with(binding) {
            if (item.playlistCoverUri!= null) {
                playlistCover.setImageURI(item.playlistCoverUri.toUri())
            } else {
                playlistCover.setImageResource(R.drawable.ic_placeholder)
            }
            playlistName.text = item.playlistName
            playlistTracksCount.text = getCorrectTracksName(item.tracksCount)
        }
    }

    private fun getCorrectTracksName(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count ${context.getString(R.string.tracks_one)}"
            count % 10 in 2..4 && (count % 100 < 12 || count % 100 > 14) -> "$count ${context.getString(R.string.tracks_2_3_4)}"
            else -> "$count ${context.getString(R.string.tracks_other)}"
        }
    }
}