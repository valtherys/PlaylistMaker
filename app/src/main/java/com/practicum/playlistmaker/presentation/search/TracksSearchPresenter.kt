package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.models.TracksResponse
import com.practicum.playlistmaker.presentation.player.ResultType
import com.practicum.playlistmaker.presentation.ui.search.SearchView

class TracksSearchPresenter(private val interactor: TracksSearchInteractor) {
    private var view: SearchView? = null

    fun attachView(view: SearchView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun onSearchRequested(expression: String) {
        if (expression.isNotBlank()) {
            view?.showLoader()
//            view?.clearTracks()

            interactor.searchTracks(expression, object : TracksSearchInteractor.TracksConsumer {
                override fun consume(response: TracksResponse) {
                    view?.hideLoader()

                    when (response.resultType) {
                        ResultType.EMPTY -> view?.showEmptyState()
                        ResultType.SUCCESS -> view?.showFoundTracks(response.tracks)
                        ResultType.CONNECTION -> view?.showErrorState("", true)
                        ResultType.ERROR -> view?.showErrorState(
                            response.resultCode.toString(),
                            false
                        )
                    }
                }
            })
        }
    }
}