package com.practicum.playlistmaker.ui.common.composable_items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track


val track = Track(
    trackName = "Smells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen Spirit",
    artistName = "Nirvana",
    trackTime = "5:01",
    artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
    trackId = "wkefhwue",
    collectionName = "Teen Spirit",
    releaseDate = "1999",
    primaryGenreName = "rock",
    country = "Great Britain",
    previewUrl = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
    isFavorite = false
)

@Composable
fun TrackItem(track: Track, onTrackClick: (Track) -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                onClick = { onTrackClick(track) }
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100 ?: painterResource(R.drawable.ic_placeholder_45),
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_placeholder_45),
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1F),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = track.trackName ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F, fill = false),
                    text = track.artistName ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(13.dp), contentDescription = null,
                    painter = painterResource(R.drawable.ic_black_circle_13),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = track.trackTime ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier.size(24.dp), contentDescription = null,
            painter = painterResource(R.drawable.ic_forward_arrow_24),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun TrackPreview() {
    TrackItem(track, onTrackClick = {}, Modifier)
}
