package com.practicum.playlistmaker.data.search.impl

import com.practicum.playlistmaker.data.search.TrackResponse
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.search.SearchRepository

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTrack(queryStr: String): List<Track>? {
        val response = networkClient.doRequest(TrackSearchRequest(queryStr))

        if (response.resultCode == 200) {
            return (response as TrackResponse).results
        } else {
            return null
        }

    }
}