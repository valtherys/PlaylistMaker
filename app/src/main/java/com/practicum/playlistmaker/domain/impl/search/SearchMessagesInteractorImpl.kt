package com.practicum.playlistmaker.domain.impl.search

import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesRepository

class SearchMessagesInteractorImpl(val repository: SearchMessagesRepository) :
    SearchMessagesInteractor {
    override fun getEmptyStateMessage(): String {
        return repository.getEmptyStateMessage()
    }

    override fun getConnectionErrorMessage(): String {
        return repository.getConnectionErrorMessage()
    }

    override fun getErrorMessage(): String {
        return repository.getErrorMessage()
    }
}