package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.model.PlayerState
import com.practicum.playlistmaker.domain.api.PlayTrackInteractor
import com.practicum.playlistmaker.data.api.PlayTrackRepository

class PlayTrackInteractorImpl(private val repository: PlayTrackRepository, ): PlayTrackInteractor{

    override fun preparePlayer(url: String) {
        repository.prepare(url)
    }

    override fun startPlayer() {
        repository.start()
    }

    override fun pausePlayer() {
        repository.pause()
    }

    override fun stopPlayer() {
        repository.stop()
    }

    override fun setTrackCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }

    override fun getPlayerState(): PlayerState {
        return repository.getPlayerState()
    }
}