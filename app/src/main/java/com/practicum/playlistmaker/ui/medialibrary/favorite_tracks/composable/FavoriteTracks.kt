package com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.composable_items.Placeholder
import com.practicum.playlistmaker.ui.common.composable_items.TracksLazyColumn
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoritesState

@Composable
fun FavoriteTracks(viewModel: FavoriteTracksViewModel, onTrackClick: (Track) -> Unit) {
    val state = viewModel.observeFavorites().observeAsState().value

    when (state) {
        is FavoritesState.Content -> {
            TracksLazyColumn(
                state.favoriteTracks,
                onTrackCLick = onTrackClick
            )
        }

        is FavoritesState.Empty -> {
            Placeholder(
                imageRes = R.drawable.ic_nothing_found_85,
                placeholderText = stringResource(R.string.empty_selected_tracks),
            )
        }

        null -> {}
    }
}