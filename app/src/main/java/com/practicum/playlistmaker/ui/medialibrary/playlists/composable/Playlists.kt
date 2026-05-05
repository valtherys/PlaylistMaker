package com.practicum.playlistmaker.ui.medialibrary.playlists.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.composable_items.ButtonItem
import com.practicum.playlistmaker.ui.common.composable_items.Placeholder
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsState
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel

@Composable
fun Playlists(
    viewModel: PlaylistsViewModel,
    onNewPlaylistClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    val playlistState = viewModel.playlistsLiveData.observeAsState().value

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        ButtonItem(
            text = stringResource(R.string.new_playlist),
            onClickAction = onNewPlaylistClick
        )
        Spacer(modifier = Modifier.height(14.dp))
        when (playlistState) {
            is PlaylistsState.Content -> {
                LazyVerticalGrid(
                    modifier = Modifier.weight(1F),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = playlistState.playlists,
                        key = { it.playlistId ?: Int.MAX_VALUE }) { playlist ->
                        PlaylistItem(playlist, onPlaylistClick, modifier = Modifier.animateItem())
                    }
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            PlaylistsState.Empty -> Placeholder(
                imageRes = R.drawable.ic_nothing_found_120,
                placeholderText = stringResource(R.string.empty_playlists),
            )

            null -> {}
        }
    }
}