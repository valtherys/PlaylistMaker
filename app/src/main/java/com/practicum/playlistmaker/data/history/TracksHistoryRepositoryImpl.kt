package com.practicum.playlistmaker.data.history

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.mappers.TrackDtoMapper
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksHistory

class TracksHistoryRepositoryImpl(
    private val storage: StorageClient<List<TrackDto>>,
    private val trackDtoMapper: TrackDtoMapper,
) :
    TracksHistoryRepository {
    private var tracksHistory = mutableListOf<TrackDto>()

    override fun readTracksHistory() {
        tracksHistory = storage.getData()?.toMutableList() ?: mutableListOf()
    }

    override suspend fun getTracksHistory(): TracksHistory {
        try {
            val tracks = convertFromTrackDto(tracksHistory)
            return TracksHistory(tracks)
        } catch (e: Exception) {
            return TracksHistory(listOf())
        }
    }

    override fun saveTrackInHistory(track: Track) {
        val trackDto = trackDtoMapper.map(track)
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

    private fun convertFromTrackDto(
        tracks: List<TrackDto>
    ): List<Track> {
        return tracks.map { track ->
            trackDtoMapper.map(track)
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}