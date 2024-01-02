package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.model.Track

interface SearchInteractor {
    fun searchTrack(queryString: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}