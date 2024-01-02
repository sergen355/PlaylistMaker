package com.practicum.playlistmaker.presentation.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.PlayTrackInteractor
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.model.PlayerState
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val PLAY_DEBOUNCE_DELAY = 1000L
    }

    private var currentPlayPosition: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private val interactor: PlayTrackInteractor = Creator.providePlayTrackInteractor()


    private val timeChangeRunnable = increasePlayTime()

    private lateinit var play: ImageView
    private lateinit var playbackTime: TextView


    private fun increasePlayTime() :Runnable {

        return object : Runnable {
            override fun run() {
                currentPlayPosition++
                val temp: String = if (currentPlayPosition < 10) {
                    "00:0$currentPlayPosition"
                } else {
                    "00:$currentPlayPosition"
                }
                playbackTime.text = temp
                handler.postDelayed(this, PLAY_DEBOUNCE_DELAY)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        interactor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.stopPlayer()
    }

    private fun playbackControl() {
        when (interactor.getPlayerState()) {
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                handler.removeCallbacks(timeChangeRunnable)
                setPauseIcon()
                interactor.startPlayer()
                handler.postDelayed(timeChangeRunnable, PLAY_DEBOUNCE_DELAY)
            }

            PlayerState.STATE_PLAYING -> {
                handler.removeCallbacks(timeChangeRunnable)
                setPlayIcon()
                interactor.pausePlayer()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        play = findViewById(R.id.play)
        playbackTime = findViewById(R.id.playback_time)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            this.finish()
        }

        val track = intent.getSerializableExtra("track") as? Track
        val trackUrl = if (track?.previewUrl == null) {
            ""
        } else {
            track.previewUrl
        }

        preparePlayer(trackUrl)

        val trackTime =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)

        play.setOnClickListener {
            playbackControl()
        }

        fun getAlbumCover() = track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

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
        duration.text = trackTime
        album.text = track?.collectionName
        year.text = track?.releaseDate?.substring(0, 4)
        genre.text = track?.primaryGenreName
        country.text = track?.country

        Glide.with(this).load(getAlbumCover()).placeholder(R.drawable.album_placeholder_large)
            .centerInside()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_corner_radius)))
            .into(albumCover)

    }

    private fun preparePlayer(url: String) {
        interactor.preparePlayer(url)
        handler.removeCallbacks(timeChangeRunnable)
        setPlayIcon()
        play.isEnabled = true
        currentPlayPosition = 0
        getTrackOnCompletionListener()
    }

    private fun getTrackOnCompletionListener() {
        interactor.setTrackCompletionListener {
            setPlayIcon()
            currentPlayPosition = 0
            handler.removeCallbacks(timeChangeRunnable)
            playbackTime.text = getString(R.string.zero_time)
        }
    }

    private fun setPlayIcon() {
        play.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play, null))
    }

    private fun setPauseIcon() {
        play.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null))
    }

}