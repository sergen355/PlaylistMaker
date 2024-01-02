package com.practicum.playlistmaker.data.api

import com.practicum.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getHistoryList()
    fun clearHistory()
    fun clearTrackList()
    fun getTrackList() : MutableList<Track>

}