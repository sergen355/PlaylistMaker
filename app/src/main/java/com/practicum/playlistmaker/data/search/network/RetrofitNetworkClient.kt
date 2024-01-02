package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.ITunesApi
import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val appleBaseUrl = "https://itunes.apple.com"

    private val retrofit =
        Retrofit.Builder().baseUrl(appleBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    private val appleTrackService = retrofit.create(ITunesApi::class.java)

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