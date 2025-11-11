package com.practicum.playlistmaker.domain.api.search

import com.practicum.playlistmaker.R

interface SearchMessagesInteractor {
    fun getEmptyStateMessage(): String
    fun getConnectionErrorMessage(): String
    fun getErrorMessage(): String
}