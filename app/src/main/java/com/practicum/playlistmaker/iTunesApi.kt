package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {

    // https://itunes.apple.com/search?entity=song&term=michael
    @GET("search")
    fun search(
        @Query("entity") entity: String,
        @Query("term") text: String) : Call<TrackResponse>

}