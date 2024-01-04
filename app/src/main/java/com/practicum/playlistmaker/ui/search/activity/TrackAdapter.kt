package com.practicum.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.Track

class TrackAdapter(
    private val clickTrackListener: ClickTrackListener,
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickTrackListener.onTrackClick(tracks[position].trackId)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface ClickTrackListener {
        fun onTrackClick(trackId: Int)
    }

}