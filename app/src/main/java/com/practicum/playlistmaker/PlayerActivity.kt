package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            this.finish()
        }

        val track = intent.getSerializableExtra("track") as? Track
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)

        fun getAlbumCover() = track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        val artist = findViewById<TextView>(R.id.artist)
        val track_name = findViewById<TextView>(R.id.track)
        val playback_time = findViewById<TextView>(R.id.playback_time)
        val album = findViewById<TextView>(R.id.album)
        val year = findViewById<TextView>(R.id.year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        val duration = findViewById<TextView>(R.id.duration)

        val albumCover: ImageView = findViewById(R.id.album_cover)

        artist.text = track?.artistName
        track_name.text = track?.trackName
        playback_time.text = trackTime
        duration.text = trackTime
        album.text = track?.collectionName
        year.text = track?.releaseDate?.substring(0, 4)
        genre.text = track?.primaryGenreName
        country.text = track?.country

        Glide.with(this)
            .load(getAlbumCover())
            .placeholder(R.drawable.album_placeholder_large)
            .centerInside()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(albumCover)

    }
}