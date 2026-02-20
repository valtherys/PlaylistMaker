package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int? = null,
    val playlistName: String,
    val playlistDescription: String?,
    val coverFilePath: String?,
    var trackIds: List<String>?,
    var tracksAmount: Int = 0,
)