package com.practicum.playlistmaker.ui.medialibrary.playlists.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist

@Composable
fun PlaylistItem(playlist: Playlist, onPlaylistClick: (Playlist) -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = {
                onPlaylistClick(
                    playlist
                )
            }), horizontalAlignment = Alignment.Start
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(R.drawable.ic_placeholder_45),
            contentScale = ContentScale.Crop,
            model = playlist.coverFilePath ?: R.drawable.ic_placeholder_45,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = playlist.playlistName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = pluralStringResource(
                R.plurals.track_count,
                count = playlist.tracksAmount,
                playlist.tracksAmount
            ), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}