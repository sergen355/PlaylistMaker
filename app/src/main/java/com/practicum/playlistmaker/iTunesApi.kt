package com.practicum.playlistmaker

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface iTunesApi {

    @GET("hamsters")
    fun getTracks(): Call<List<Track>>

    @POST("search/{id}/update")
    fun search(@Query("term") text: String) : Call<TrackResponse>

}