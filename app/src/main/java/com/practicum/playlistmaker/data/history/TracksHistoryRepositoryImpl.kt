package com.practicum.playlistmaker.data.history

import android.util.Log
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.TracksHistory
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksHistoryRepositoryImpl(private val searchHistory: SearchHistory) : TracksHistoryRepository {
    override fun readTracksHistory() {
        searchHistory.readTracksHistory()
    }

    override fun getTracksHistory(): TracksHistory {
        val tracksRow = searchHistory.getTracksHistoryCopy()
        try {
            val tracks = tracksRow.map {
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
            } as ArrayList<Track>
            return TracksHistory(tracks)
        } catch (e: Exception) {
            Log.e("HistoryRepo", "Failed to create Track for rawData=$tracksRow", e)
            return TracksHistory(ArrayList())
        }
    }

    override fun saveTrackInHistory(track: Track) {
       val trackDto = TrackDto(trackName = track.trackName,
           artistName = track.artistName,
           trackTime = track.trackTime,
           artworkUrl100 = track.artworkUrl100,
           trackId = track.trackId,
           collectionName = track.collectionName,
           releaseDate = track.releaseDate,
           primaryGenreName = track.primaryGenreName,
           country = track.country,
           previewUrl = track .previewUrl)
        searchHistory.saveTrackInHistory(trackDto)
    }

    override fun clearTrackHistory() {
        searchHistory.clearTracksHistory()
    }
}