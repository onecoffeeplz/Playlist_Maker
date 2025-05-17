package com.example.playlistmaker.media.ui

import android.content.Context
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val context: Context = itemView.context

    fun bind(item: Playlist) {
        with(binding) {
            if (item.playlistCoverUri != null) {
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