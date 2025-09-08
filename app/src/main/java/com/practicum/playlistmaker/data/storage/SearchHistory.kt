package com.practicum.playlistmaker.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.model.Track
import androidx.core.content.edit

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    val tracksHistory = arrayListOf<Track>()

    fun readTracksHistory() {
        tracksHistory.clear()
        val json = sharedPrefs.getString(TRACKS_HISTORY_KEY, null) ?: return
        if (!json.isBlank()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            tracksHistory.addAll(Gson().fromJson(json, type))
        }
    }

    fun addTrackInTracksHistory(track: Track) {
        tracksHistory.removeAll { it.trackId == track.trackId }
        tracksHistory.add(track)
        if (tracksHistory.size > 10) {
            tracksHistory.removeAt(0)
        }
    }

    fun saveTracksHistory() {
        val tracksJson = Gson().toJson(tracksHistory)
        sharedPrefs.edit { putString(TRACKS_HISTORY_KEY, tracksJson) }
    }

    fun clearTracksHistory() {
        tracksHistory.clear()
        sharedPrefs.edit { remove(TRACKS_HISTORY_KEY) }
    }

    companion object {
        const val TRACKS_HISTORY_KEY = "TRACKS_HISTORY"
    }
}