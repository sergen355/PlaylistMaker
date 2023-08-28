package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackDur: TextView = itemView.findViewById(R.id.track_dur)
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun bind(model: Track) {

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackDur.text = model.trackTime

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.pm_placeholder)
            .centerInside()
            .transform(RoundedCorners(dpToPx(2F, itemView.context)))
            .into(trackImage)
    }

}