package com.practicum.playlistmaker.domain.api.search

interface SearchMessagesInteractor {
    fun getEmptyStateMessage(): String
    fun getConnectionErrorMessage(): String
    fun getErrorMessage(): String
}