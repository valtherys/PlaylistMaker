package com.practicum.playlistmaker.ui.search.view_model

import com.practicum.playlistmaker.domain.models.Track

sealed interface HistoryState {
    data class Content(val tracks: List<Track>): HistoryState
    object Hidden: HistoryState
}