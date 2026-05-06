package com.practicum.playlistmaker.ui.search.composable.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.search.composable.CustomTextField
import com.practicum.playlistmaker.ui.common.composable.Loader
import com.practicum.playlistmaker.ui.common.composable.Placeholder
import com.practicum.playlistmaker.ui.search.composable.TracksHistory
import com.practicum.playlistmaker.ui.common.composable.TracksLazyColumn
import com.practicum.playlistmaker.ui.search.view_model.TracksState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel

@Composable
fun SearchScreen(viewModel: TracksViewModel, onTrackClickDebounce: (Track) -> Unit) {
    val text = viewModel.text.collectAsState().value
    val state = viewModel.tracksState.collectAsState().value

    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 14.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.search),
            modifier = Modifier.height(56.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            text = text,
            onTextChange = { viewModel.onTextChanged(it) },
            onClear = { viewModel.cancelSearch() },
            onFocusAction = { viewModel.onShowTracksHistory() },
        )

        Spacer(modifier = Modifier.height(24.dp))
        when (state) {
            is TracksState.Loading -> Loader()
            is TracksState.Connection -> Placeholder(
                placeholderText = state.message,
                onClickAction = { viewModel.onRenewBtnClick() },
                imageRes = R.drawable.ic_network_issues_120,
                btnText = R.string.renew
            )

            is TracksState.Empty -> Placeholder(
                imageRes = R.drawable.ic_nothing_found_85,
                placeholderText = stringResource(R.string.nothing_found),
            )

            is TracksState.Error -> Placeholder(
                placeholderText = state.message,
                imageRes = R.drawable.ic_network_issues_120,
            )

            TracksState.HiddenHistory -> {}
            is TracksState.HistoryContent ->
                if (state.tracks.isEmpty()) {
                } else {
                    TracksHistory(
                        tracks = state.tracks,
                        onBtnClickAction = { viewModel.onDeleteTracksHistory() },
                        onTrackClick = onTrackClickDebounce
                    )
                }

            is TracksState.SearchContent -> TracksLazyColumn(
                state.tracks,
                onTrackCLick = onTrackClickDebounce,
            )
        }
    }
}
