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
            playlistTracksCount.text = context.resources.getQuantityString(
                R.plurals.tracks_count,
                item.tracksCount,
                item.tracksCount)
        }
    }
}