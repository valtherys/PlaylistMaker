package com.practicum.playlistmaker.domain.models

data class Playlist(
    var playlistId: Int? = null,
    val playlistName: String,
    val playlistDescription: String?,
    val coverFilePath: String?,
    var trackIds: List<String>?,
    var tracksAmount: Int,
)