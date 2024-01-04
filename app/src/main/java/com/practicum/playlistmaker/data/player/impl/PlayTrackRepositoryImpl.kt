package com.practicum.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.model.PlayerState
import com.practicum.playlistmaker.domain.player.PlayTrackRepository

class PlayTrackRepositoryImpl : PlayTrackRepository {

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT

    override fun prepare(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        playerState = PlayerState.STATE_PREPARED
    }

    override fun start() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun stop() {
        mediaPlayer.stop()
        playerState = PlayerState.STATE_DEFAULT
        mediaPlayer.release()
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            listener.invoke()
        }
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }
}