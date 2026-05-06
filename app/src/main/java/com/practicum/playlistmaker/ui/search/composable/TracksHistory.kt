package com.practicum.playlistmaker.ui.search.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.composable.ButtonItem
import com.practicum.playlistmaker.ui.common.composable.TrackItem

@Composable
fun TracksHistory(
    tracks: List<Track>,
    onBtnClickAction: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = stringResource(R.string.you_searched),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(
            items = tracks,
            key = { it.trackId }) { track ->
            TrackItem(track, onTrackClick, Modifier.animateItem())
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ButtonItem(
                text = stringResource(R.string.clear_history),
                onClickAction = onBtnClickAction
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

