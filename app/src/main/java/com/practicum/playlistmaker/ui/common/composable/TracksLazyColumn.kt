package com.practicum.playlistmaker.ui.common.composable

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.theme.AppTheme

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

private fun loadTracks(context: Context): List<Track> {
    val json = context.assets.open("tracks.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Track>>() {}.type
    return Gson().fromJson(json, type)
}

@Preview(showSystemUi = true)
@Composable
private fun TracksLazyColumnPreview() {
    val context = LocalContext.current
    val tracks = loadTracks(context)
    AppTheme {
        TracksLazyColumn(tracks, {})
    }
}