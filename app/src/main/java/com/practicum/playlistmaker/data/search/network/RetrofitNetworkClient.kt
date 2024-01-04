package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.ITunesApi
import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest

class RetrofitNetworkClient(private val appleTrackService: ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val resp = appleTrackService.search("song", dto.queryString).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = 400 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }

    }

}