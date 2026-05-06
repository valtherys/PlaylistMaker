package com.practicum.playlistmaker.ui.medialibrary.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.composable.FavoriteTracks
import com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.medialibrary.playlists.composable.Playlists
import com.practicum.playlistmaker.ui.medialibrary.playlists.view_model.PlaylistsViewModel
import kotlinx.coroutines.launch

@Composable
fun MediaLibraryScreen(
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit,
    tracksViewModel: FavoriteTracksViewModel,
    playlistsViewModel: PlaylistsViewModel
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = pagerState.currentPage
    LaunchedEffect(Unit) {
        tracksViewModel.getFavoriteTracks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 14.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.media_library),
            modifier = Modifier.height(56.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .padding(horizontal = 16.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            divider = {}
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.selected_tracks),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )

            Tab(
                selected = selectedTabIndex == 1,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.playlists),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> FavoriteTracks(viewModel = tracksViewModel, onTrackClick = onTrackClick)
                1 -> Playlists(
                    viewModel = playlistsViewModel,
                    onNewPlaylistClick = onNewPlaylistClick,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }
    }
}
