package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.data.dto.adapters.TrackTimeAdapter

data class TrackDto(
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis")
    @JsonAdapter(TrackTimeAdapter::class)
    val trackTime: String?,
    val artworkUrl100: String?,
    val trackId: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)
