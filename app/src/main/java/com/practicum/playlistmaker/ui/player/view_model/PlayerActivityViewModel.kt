package com.practicum.playlistmaker.ui.player.view_model

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.model.PlayerState
import com.practicum.playlistmaker.domain.model.PlayingStatus
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.player.PlayTrackInteractor

class PlayerActivityViewModel(private val playTrackInteractor: PlayTrackInteractor) : ViewModel() {

    private val PLAY_DEBOUNCE_DELAY = 1000L

    private var _playingStatusMutable = MutableLiveData<PlayingStatus>()
    val playingStatus: LiveData<PlayingStatus> = _playingStatusMutable

    private var _currentPlayPositionMutable = MutableLiveData<Int>()
    val currentPlayPosition: LiveData<Int> = _currentPlayPositionMutable

    private var _trackMutable = MutableLiveData<Track>()
    val track: LiveData<Track> = _trackMutable

    private val handler = Handler(Looper.getMainLooper())
    private val timeChangeRunnable = increasePlayTime()


    private fun increasePlayTime(): Runnable {

        return object : Runnable {
            override fun run() {
                _currentPlayPositionMutable.value = _currentPlayPositionMutable.value?.plus(1)
                handler.postDelayed(this, PLAY_DEBOUNCE_DELAY)
            }
        }
    }


    fun preparePlayer(intent: Intent) {
        _trackMutable.value = intent.getSerializableExtra("track") as? Track

        var trackUrl = ""
        trackUrl = if (_trackMutable.value?.previewUrl == null) {
            ""
        } else {
            _trackMutable.value?.previewUrl
        }!!

        _playingStatusMutable.value = PlayingStatus.PLAY
        handler.removeCallbacks(timeChangeRunnable)
        playTrackInteractor.preparePlayer(trackUrl)
        _currentPlayPositionMutable.value = 0


        playTrackInteractor.setTrackCompletionListener {
            _playingStatusMutable.value = PlayingStatus.PLAY
            _currentPlayPositionMutable.value = 0
            handler.removeCallbacks(timeChangeRunnable)
        }

    }

    fun pausePlayer() {
        playTrackInteractor.pausePlayer()
    }

    fun stopPlayer() {
        playTrackInteractor.stopPlayer()
    }

    fun playbackControl() {
        when (playTrackInteractor.getPlayerState()) {
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                handler.removeCallbacks(timeChangeRunnable)
                _playingStatusMutable.value = PlayingStatus.PAUSE
                playTrackInteractor.startPlayer()
                handler.postDelayed(timeChangeRunnable, PLAY_DEBOUNCE_DELAY)
            }

            PlayerState.STATE_PLAYING -> {
                handler.removeCallbacks(timeChangeRunnable)
                _playingStatusMutable.value = PlayingStatus.PLAY
                playTrackInteractor.pausePlayer()
            }
        }

    }


    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerActivityViewModel(
                        Creator.providePlayTrackInteractor()
                    ) as T
                }
            }
    }

}