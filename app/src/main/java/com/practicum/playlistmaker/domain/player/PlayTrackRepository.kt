package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.model.PlayerState

interface PlayTrackRepository {

    fun prepare(url: String)
    fun start()
    fun pause()
    fun stop()
    fun setOnCompletionListener(listener: () -> Unit)
    fun getPlayerState(): PlayerState
}