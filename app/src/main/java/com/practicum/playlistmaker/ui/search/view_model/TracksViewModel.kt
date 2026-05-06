package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TracksViewModel(
    val searchInteractor: TracksSearchInteractor,
    val historyInteractor: TracksHistoryInteractor,
    val searchMessagesInteractor: SearchMessagesInteractor,
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val text: StateFlow<String> = _searchText.asStateFlow()
    private val _tracksState = MutableStateFlow<TracksState>(TracksState.SearchContent(listOf()))
    private var latestSearchText: String? = null
    private var searchJob: Job? = null
    val tracksState: StateFlow<TracksState> = _tracksState.asStateFlow()

    val onSearchDebounce = debounce<String>(
        delayMillis = SEARCH_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true,
        action = { request -> onSearchRequested(request) }
    )

    init {
        historyInteractor.readTracksHistory()
    }

    fun onTextChanged(newText: String) {
        _searchText.value = newText
        searchDebounce(newText)
    }

    fun onSearchRequested(expression: String) {
        if (expression.isNotBlank()) {
            _tracksState.value = TracksState.Loading
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                searchInteractor.searchTracks(expression).collect {
                    when (it.resultType) {
                        ResultType.EMPTY -> _tracksState.value =
                            TracksState.Empty(
                                searchMessagesInteractor.getEmptyStateMessage()
                            )

                        ResultType.SUCCESS -> _tracksState.value =
                            TracksState.SearchContent(
                                it.tracks
                            )

                        ResultType.CONNECTION -> _tracksState.value =
                            TracksState.Connection(
                                searchMessagesInteractor.getConnectionErrorMessage()
                            )

                        ResultType.ERROR -> _tracksState.value =
                            TracksState.Error(
                                String.format(
                                    searchMessagesInteractor.getErrorMessage(),
                                    it.resultCode.toString()
                                )
                            )
                    }
                }
            }
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
        onShowTracksHistory()
    }

    fun onShowTracksHistory() {
        _tracksState.value = TracksState.HiddenHistory

        viewModelScope.launch {
            historyInteractor.getTracksFromHistory(object :
                TracksHistoryInteractor.TracksHistoryConsumer {
                override fun consume(tracks: List<Track>) {
                    if (tracks.isEmpty()) {
                        _tracksState.value = TracksState.HiddenHistory
                    } else {
                        _tracksState.value = TracksState.HistoryContent(tracks.reversed())
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
        _tracksState.value = TracksState.HiddenHistory
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            onSearchDebounce(changedText)
        }
    }

    fun onRenewBtnClick() {
        onSearchRequested(text.value)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}