package com.practicum.playlistmaker.domain.impl.history

import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository):
    TracksHistoryInteractor {
    override fun readTracksHistory(){
        repository.readTracksHistory()
    }

    override fun getTracksFromHistory(consumer: TracksHistoryInteractor.TracksHistoryConsumer) {
        consumer.consume(repository.getTracksHistory().tracks)
    }

    override fun saveTrackInHistory(track: Track) {
        repository.saveTrackInHistory(track)
    }

    override fun deleteTracksHistory() {
        repository.clearTracksHistory()
    }
}