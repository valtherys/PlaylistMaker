package com.practicum.playlistmaker.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkModule {
    private const val BASE_URL = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
        GsonConverterFactory.create()
    ).build()

    val iTunesService: ITunesAPI = retrofit.create(ITunesAPI::class.java)
}
