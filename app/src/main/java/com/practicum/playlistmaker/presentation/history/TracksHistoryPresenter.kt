package com.practicum.playlistmaker.presentation.history

import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.search.SearchHistoryView

class TracksHistoryPresenter(private val interactor: TracksHistoryInteractor) {
    private var view: SearchHistoryView? = null

    fun attachView(view: SearchHistoryView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun onShowTracksHistory(){
        interactor.getTracksFromHistory(object : TracksHistoryInteractor.TracksHistoryConsumer{
            override fun consume(tracks: ArrayList<Track>) {
                view?.clearTracks()
                if (tracks.isEmpty()){
                    view?.hideTracksHistory()
                } else {
                    view?.showTracksHistory(ArrayList(tracks.reversed()))
                }
            }
        })
    }

    fun onSaveTrackInHistory(track: Track){
        interactor.saveTrackInHistory(track)
    }

    fun onDeleteTracksHistory(){
        interactor.deleteTracksHistory()
        view?.hideTracksHistory()
    }

    fun onReadTracksHistory() {
        interactor.readTracksHistory()
    }
}