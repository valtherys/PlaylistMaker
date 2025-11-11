package com.practicum.playlistmaker.domain.api.search

interface SearchMessagesRepository {
    fun getEmptyStateMessage(): String
    fun getConnectionErrorMessage(): String
    fun getErrorMessage(): String
}