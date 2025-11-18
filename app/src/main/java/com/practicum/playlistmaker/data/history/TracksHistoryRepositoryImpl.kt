package com.practicum.playlistmaker.data.history

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.TracksHistory
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksHistoryRepositoryImpl(private val storage: StorageClient<List<TrackDto>>) :
    TracksHistoryRepository {
    private var tracksHistory = mutableListOf<TrackDto>()

    override fun readTracksHistory() {
        tracksHistory = storage.getData()?.toMutableList() ?: mutableListOf()
    }

    override fun getTracksHistory(): TracksHistory {
        try {
            val tracks = tracksHistory.map {
                Track(
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = it.trackTime,
                    artworkUrl100 = it.artworkUrl100,
                    trackId = it.trackId,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
            return TracksHistory(tracks)
        } catch (e: Exception) {
            return TracksHistory(listOf())
        }
    }

    override fun saveTrackInHistory(track: Track) {
        val trackDto = TrackDto(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        addTrackInTracksHistory(trackDto)
        storage.storeData(tracksHistory.toList())
    }

    override fun clearTracksHistory() {
        tracksHistory.clear()
        storage.clearData()
    }

    fun addTrackInTracksHistory(track: TrackDto) {
        tracksHistory.removeAll { it.trackId == track.trackId }
        tracksHistory.add(track)
        if (tracksHistory.size > MAX_HISTORY_SIZE) {
            tracksHistory.removeAt(0)
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}