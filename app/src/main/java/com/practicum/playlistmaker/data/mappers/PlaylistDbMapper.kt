package com.practicum.playlistmaker.data.mappers

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistDbMapper {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            coverFilePath = playlist.coverFilePath,
            trackIds = playlist.trackIds,
            tracksAmount = playlist.tracksAmount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            coverFilePath = playlist.coverFilePath,
            trackIds = playlist.trackIds,
            tracksAmount = playlist.tracksAmount
        )
    }
}