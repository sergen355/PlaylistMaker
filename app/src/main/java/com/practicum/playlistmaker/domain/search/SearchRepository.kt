package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.model.Track

interface SearchRepository {
    fun searchTrack(queryStr: String): List<Track>?
}