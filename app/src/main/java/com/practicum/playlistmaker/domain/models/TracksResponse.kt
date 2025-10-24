package com.practicum.playlistmaker.domain.models

import com.practicum.playlistmaker.presentation.player.ResultType

data class TracksResponse (
    val resultCode: Int,
    val tracks: ArrayList<Track>,
    val resultType: ResultType
)