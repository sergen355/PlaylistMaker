package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAY_DEBOUNCE_DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private var currentPlayPosition: Int = 0
    private val mediaPlayer = MediaPlayer()
    private val timeChangeRunnable = Runnable { increasePlayTime() }

    private lateinit var play: ImageView
    private lateinit var playbackTime: TextView

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.pause))
        playerState = STATE_PLAYING
        handler.postDelayed(timeChangeRunnable, PLAY_DEBOUNCE_DELAY)

        mediaPlayer.setOnCompletionListener {
            play.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.play))
            playerState = STATE_PREPARED
            handler.removeCallbacks(timeChangeRunnable)
            playbackTime.text = resources.getString(R.string.zero_time)
            currentPlayPosition = 0
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.play))
        playerState = STATE_PAUSED
        handler.removeCallbacksAndMessages(timeChangeRunnable)
    }

    private fun increasePlayTime() {
        if (playerState == STATE_PLAYING) {
            currentPlayPosition++
            val temp: String = if (currentPlayPosition < 10) {
                "00:0$currentPlayPosition"
            } else {
                "00:$currentPlayPosition"
            }

            playbackTime.text = temp
            handler.postDelayed({ increasePlayTime() }, PLAY_DEBOUNCE_DELAY)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
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
        val trackTime =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)

        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }

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
}