package com.practicum.playlistmaker.domain.models

import com.practicum.playlistmaker.ui.search.view_model.ResultType

data class TracksResponse(
    val resultCode: Int,
    val tracks: List<Track>,
    val resultType: ResultType
)