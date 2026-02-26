package com.practicum.playlistmaker.data.sharing

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.sharing.PlaylistMessageBuilderRepository
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

class PlaylistMessageBuilderRepositoryImpl(private val context: Context) :
    PlaylistMessageBuilderRepository {
    override fun buildMessage(playlist: Playlist, tracks: List<Track>): String {
        val tracksAmountString = context.resources.getQuantityString(
            R.plurals.track_count, playlist.tracksAmount
        ).format(playlist.tracksAmount)

        val message = buildString {
            appendLine(playlist.playlistName)
            if (!playlist.playlistDescription.isNullOrEmpty()) {
                appendLine(playlist.playlistDescription)
            }
            appendLine(tracksAmountString)
            appendLine()
            tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
            }
        }

        return message
    }
}