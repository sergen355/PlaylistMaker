package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    @GET("search")
    fun search(
        @Query("entity") entity: String,
        @Query("term") text: String) : Call<TrackResponse>

}