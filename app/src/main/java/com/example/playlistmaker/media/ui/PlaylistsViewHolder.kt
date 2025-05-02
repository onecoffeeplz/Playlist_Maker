package com.example.playlistmaker.media.ui

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.domain.models.Playlist
import java.text.NumberFormat
import java.util.Locale

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        with(binding) {
            if (item.playlistCoverUri != null) {
                playlistCover.setImageURI(item.playlistCoverUri.toUri())
            } else {
                playlistCover.setImageResource(R.drawable.ic_placeholder)
            }
            playlistName.text = item.playlistName
            playlistTracksCount.text = convertIntToStringWithLocale(item.tracksCount)
        }
    }

    private fun convertIntToStringWithLocale(intValue: Int): String {
        val numberFormat = NumberFormat.getInstance(Locale.getDefault())
        return numberFormat.format(intValue)
    }
}