package com.practicum.playlistmaker.presentation.ui.mappers

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.models.TrackParcelable

fun Track.toParcelable(): TrackParcelable = TrackParcelable(
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    trackId = trackId,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)