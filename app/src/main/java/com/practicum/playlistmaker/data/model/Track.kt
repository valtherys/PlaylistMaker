package com.practicum.playlistmaker.data.model

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime


@Parcelize
data class Track(
    val trackName: String,
    val artistName: String,

    @SerializedName("trackTimeMillis")
    @JsonAdapter(TrackTimeAdapter::class)
    val trackTime: String,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
): Parcelable{
    fun getArtworkUrlHighResolution() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getReleaseYear(): String{
        val dateTime = OffsetDateTime.parse(releaseDate)
        return dateTime.year.toString()
    }
}