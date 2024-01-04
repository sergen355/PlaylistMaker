package com.practicum.playlistmaker.ui.player.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.PlayingStatus
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {


    private lateinit var playerActivityViewModel: PlayerViewModel
    private lateinit var play: ImageView
    private lateinit var playbackTime: TextView

    override fun onPause() {
        super.onPause()
        playerActivityViewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerActivityViewModel.stopPlayer()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        play = findViewById(R.id.play)
        playbackTime = findViewById(R.id.playback_time)



        playerActivityViewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory()
        )[PlayerViewModel::class.java]


        val back = findViewById<ImageView>(R.id.back)

        back.setOnClickListener {
            this.finish()
        }

        playerActivityViewModel.preparePlayer(intent)



        play.setOnClickListener {
            playerActivityViewModel.playbackControl()
        }

        playerActivityViewModel.track.observe(this) { track ->
            fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

            val artist = findViewById<TextView>(R.id.artist)
            val trackName = findViewById<TextView>(R.id.track)
            val album = findViewById<TextView>(R.id.album)
            val year = findViewById<TextView>(R.id.year)
            val genre = findViewById<TextView>(R.id.genre)
            val country = findViewById<TextView>(R.id.country)
            val duration = findViewById<TextView>(R.id.duration)

            val albumCover: ImageView = findViewById(R.id.album_cover)

            artist.text = track?.artistName
            trackName.text = track?.trackName
            duration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
            album.text = track?.collectionName
            year.text = track?.releaseDate?.substring(0, 4)
            genre.text = track?.primaryGenreName
            country.text = track?.country

            Glide.with(this)
                .load(getCoverArtwork())
                .placeholder(R.drawable.album_placeholder_large)
                .centerInside()
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
                .into(albumCover)
        }


        playerActivityViewModel.playingStatus.observe(this) { it ->
            play.isEnabled = true
            if (it == PlayingStatus.PLAY) {
                play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play))
            } else if (it == PlayingStatus.PAUSE) {
                play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause))
            }
        }

        playerActivityViewModel.currentPlayPosition.observe(this) { currentPlayPosition ->
            val temp: String = if (currentPlayPosition < 10) {
                "00:0$currentPlayPosition"
            } else {
                "00:$currentPlayPosition"
            }
            playbackTime.text = temp

        }
    }

}