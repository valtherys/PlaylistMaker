package com.practicum.playlistmaker.domain.api.sharing

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

interface PlaylistMessageBuilderRepository {
    fun buildMessage(playlist: Playlist, tracks: List<Track>): String
}