package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TracksViewModel(
    val searchInteractor: TracksSearchInteractor,
    val historyInteractor: TracksHistoryInteractor,
    val searchMessagesInteractor: SearchMessagesInteractor,
) : ViewModel() {
    private val tracksStateLiveData = MutableLiveData<TracksState>()
    private var latestSearchText: String? = null
    private var searchJob: Job? = null
    fun observeTracksStateLiveData(): LiveData<TracksState> = tracksStateLiveData

    val onSearchDebounce = debounce<String>(
        delayMillis = SEARCH_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true,
        action = { request -> onSearchRequested(request) }
    )

    init {
        historyInteractor.readTracksHistory()
    }

    fun onSearchRequested(expression: String) {
        if (expression.isNotBlank()) {
            tracksStateLiveData.postValue(TracksState.Loading)
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                searchInteractor.searchTracks(expression).collect {
                    when (it.resultType) {
                        ResultType.EMPTY -> tracksStateLiveData.postValue(
                            TracksState.Empty(
                                searchMessagesInteractor.getEmptyStateMessage()
                            )
                        )

                        ResultType.SUCCESS -> tracksStateLiveData.postValue(
                            TracksState.SearchContent(
                                it.tracks
                            )
                        )

                        ResultType.CONNECTION -> tracksStateLiveData.postValue(
                            TracksState.Connection(
                                searchMessagesInteractor.getConnectionErrorMessage()
                            )
                        )

                        ResultType.ERROR -> tracksStateLiveData.postValue(
                            TracksState.Error(
                                String.format(
                                    searchMessagesInteractor.getErrorMessage(),
                                    it.resultCode.toString()
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
    }

    fun onShowTracksHistory() {
        tracksStateLiveData.postValue(TracksState.HiddenHistory)

        viewModelScope.launch {
            historyInteractor.getTracksFromHistory(object :
                TracksHistoryInteractor.TracksHistoryConsumer {
                override fun consume(tracks: List<Track>) {
                    if (tracks.isEmpty()) {
                        tracksStateLiveData.postValue(TracksState.HiddenHistory)
                    } else {
                        tracksStateLiveData.postValue(TracksState.HistoryContent(tracks.reversed()))
                    }
                }
            })
        }
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrackInHistory(track)
    }

    fun onDeleteTracksHistory() {
        historyInteractor.deleteTracksHistory()
        tracksStateLiveData.postValue(TracksState.HiddenHistory)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            onSearchDebounce(changedText)
        }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}