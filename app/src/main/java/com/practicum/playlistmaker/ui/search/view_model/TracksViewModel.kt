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
    private val tracksStateLiveData = MutableLiveData<TracksState>()
    var searchIsNotCanceled = true
    fun observeTracksStateLiveData(): LiveData<TracksState> = tracksStateLiveData

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

    fun searchResultsDebounce(): Boolean{
        val current = searchIsNotCanceled
        if (searchIsNotCanceled) {
            searchIsNotCanceled = false
            handler.postDelayed({ searchIsNotCanceled = true },SEARCH_DEBOUNCE_DELAY)
        }
        return current
    }

    fun onSearchRequested(expression: String) {
        if (expression.isNotBlank()) {
            tracksStateLiveData.postValue(TracksState.Loading)

            searchInteractor.searchTracks(
                expression,
                object : TracksSearchInteractor.TracksConsumer {
                    override fun consume(response: TracksResponse) {
                        if (searchIsNotCanceled){
                            when (response.resultType) {
                                ResultType.EMPTY -> tracksStateLiveData.postValue(
                                    TracksState.Empty(
                                        searchMessagesInteractor.getEmptyStateMessage()
                                    )
                                )

                                ResultType.SUCCESS -> tracksStateLiveData.postValue(
                                    TracksState.SearchContent(
                                        response.tracks
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
                                            response.resultCode.toString()
                                        )
                                    )
                                )

                            }
                        } else onShowTracksHistory()
                    }
                })
        }
    }

    fun onShowTracksHistory() {
        tracksStateLiveData.postValue(TracksState.HiddenHistory)

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

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrackInHistory(track)
    }

    fun onDeleteTracksHistory() {
        historyInteractor.deleteTracksHistory()
        tracksStateLiveData.postValue(TracksState.HiddenHistory)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}