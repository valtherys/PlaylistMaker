package com.practicum.playlistmaker.data.search

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.search.SearchMessagesRepository

class SearchMessagesRepositoryImpl(val context: Context) : SearchMessagesRepository {
    override fun getEmptyStateMessage(): String {
        return context.getString(R.string.nothing_found)
    }

    override fun getConnectionErrorMessage(): String {
        return context.getString(R.string.connection_failure)
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.response_error)
    }
}