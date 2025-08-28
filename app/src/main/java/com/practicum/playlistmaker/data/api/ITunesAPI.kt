package com.practicum.playlistmaker.data.api

import com.practicum.playlistmaker.data.model.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TracksResponse>
}