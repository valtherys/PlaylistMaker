package com.practicum.playlistmaker.ui.playlist.view_model

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.UIText

data class PlaylistState(
    val playlist: Playlist? = null,
    val tracksState: PlaylistTracksState = PlaylistTracksState.Empty,
    val duration: UIText.Plural = UIText.Plural(R.plurals.minutes_count, 0)
)