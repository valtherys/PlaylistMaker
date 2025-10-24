package com.practicum.playlistmaker.domain.impl


import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.TracksResponse
import com.practicum.playlistmaker.presentation.player.ResultType
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) : TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(
        expression: String,
        consumer: TracksSearchInteractor.TracksConsumer
    ) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (e: Exception) {
                consumer.consume(TracksResponse(CONNECTION_ERROR_CODE, ArrayList(), ResultType.CONNECTION))
            }

        }
    }

    companion object {
        private const val CONNECTION_ERROR_CODE = 0
    }
}