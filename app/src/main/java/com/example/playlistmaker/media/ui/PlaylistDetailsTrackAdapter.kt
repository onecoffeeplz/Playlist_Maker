package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.models.Track

class PlaylistDetailsTrackAdapter(
    var trackList: MutableList<Track>,
    private val onTrackClickListener: OnTrackClickListener? = null,
    private val onTrackLongClickListener: OnTrackLongClickListener? = null,
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClickListener?.onTrackClick(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClickListener?.onTrackLongClick(trackList[position]) ?: false
        }
    }

    fun interface OnTrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface OnTrackLongClickListener {
        fun onTrackLongClick(track: Track): Boolean
    }
}