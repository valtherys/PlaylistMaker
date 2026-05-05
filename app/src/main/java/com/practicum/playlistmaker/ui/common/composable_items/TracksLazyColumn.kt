package com.practicum.playlistmaker.ui.common.composable_items

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.practicum.playlistmaker.domain.models.Track

@Composable
fun TracksLazyColumn(tracks: List<Track>, onTrackCLick: (Track) -> Unit) {
    LazyColumn {
        items(
            items = tracks,
            key = { it.trackId }
        ) { track ->
            TrackItem(
                track = track,
                onTrackClick = { onTrackCLick(it) },
                modifier = Modifier
            )
        }
    }
}