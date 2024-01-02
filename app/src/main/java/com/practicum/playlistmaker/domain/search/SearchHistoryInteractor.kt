package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getHistoryList()
    fun clearHistory()
    fun clearTrackList()
    fun getTrackList(): MutableList<Track>

}