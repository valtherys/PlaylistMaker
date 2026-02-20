package com.practicum.playlistmaker.data.mappers

import com.practicum.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.ui.models.TrackParcelable

class PlaylistTrackDbMapper {
        fun map(track: TrackParcelable): PlaylistTrackEntity {
            return PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                artworkUrlPlayer = track.getArtworkUrlHighResolution(),
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
            )
        }
}