package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.api.PlayTrackRepository
import com.practicum.playlistmaker.data.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.model.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :SearchHistoryInteractor {
    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistoryList() {
        repository.getHistoryList()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun clearTrackList() {
        repository.clearTrackList()
    }

    override fun getTrackList() : MutableList<Track> {
        return repository.getTrackList()
    }

}