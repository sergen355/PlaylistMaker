package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.data.search.SearchHistoryRepository
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.search.SearchHistoryInteractor

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
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

    override fun getTrackList(): MutableList<Track> {
        return repository.getTrackList()
    }

}