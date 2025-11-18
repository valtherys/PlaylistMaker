package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TracksResponse

class TracksViewModel(
    val searchInteractor: TracksSearchInteractor,
    val historyInteractor: TracksHistoryInteractor,
    val searchMessagesInteractor: SearchMessagesInteractor
) : ViewModel() {
    private val searchStateLiveData = MutableLiveData<SearchState>()
    fun observeSearchStateLiveData(): LiveData<SearchState> = searchStateLiveData
    private val historyStateLiveData = MutableLiveData<HistoryState>()
    fun observeHistoryState(): LiveData<HistoryState> = historyStateLiveData
    private var latestSearchedText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        historyInteractor.readTracksHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchedText == changedText) {
            return
        }
        this.latestSearchedText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { onSearchRequested(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun onSearchRequested(expression: String) {
        if (expression.isNotBlank()) {
            searchStateLiveData.postValue(SearchState.Loading)

            searchInteractor.searchTracks(
                expression,
                object : TracksSearchInteractor.TracksConsumer {
                    override fun consume(response: TracksResponse) {

                        when (response.resultType) {
                            ResultType.EMPTY -> searchStateLiveData.postValue(
                                SearchState.Empty(
                                    searchMessagesInteractor.getEmptyStateMessage()
                                )
                            )

                            ResultType.SUCCESS -> searchStateLiveData.postValue(
                                SearchState.Content(
                                    response.tracks
                                )
                            )

                            ResultType.CONNECTION -> searchStateLiveData.postValue(
                                SearchState.Connection(
                                    searchMessagesInteractor.getConnectionErrorMessage()
                                )
                            )

                            ResultType.ERROR -> searchStateLiveData.postValue(
                                SearchState.Error(
                                    String.format(
                                        searchMessagesInteractor.getErrorMessage(),
                                        response.resultCode.toString()
                                    )
                                )
                            )

                        }
                    }
                })
        }
    }

    fun onShowTracksHistory() {
        historyInteractor.getTracksFromHistory(object :
            TracksHistoryInteractor.TracksHistoryConsumer {
            override fun consume(tracks: List<Track>) {
                if (tracks.isEmpty()) {
                    historyStateLiveData.postValue(HistoryState.Hidden)
                } else {
                    historyStateLiveData.postValue(HistoryState.Content(tracks.reversed()))
                }
            }
        })
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrackInHistory(track)
    }

    fun onDeleteTracksHistory() {
        historyInteractor.deleteTracksHistory()
        historyStateLiveData.postValue(HistoryState.Hidden)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}