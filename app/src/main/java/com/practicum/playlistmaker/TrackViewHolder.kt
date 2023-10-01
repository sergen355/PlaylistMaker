package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackDur: TextView = itemView.findViewById(R.id.track_dur)
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackId: TextView = itemView.findViewById(R.id.track_id)

    fun bind(model: Track) {

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackDur.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        trackId.text = model.trackId.toString()

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .centerInside()
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(trackImage)
    }

}