package com.practicum.playlistmaker.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String,
    val artistName: String,

    @SerializedName("trackTimeMillis")
    @JsonAdapter(TrackTimeAdapter::class)
    val trackTime: String,
    val artworkUrl100: String
)