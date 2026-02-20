package com.practicum.playlistmaker.data.db.converter

import androidx.room.TypeConverter

class PlaylistConverter {
    @TypeConverter
    fun fromString(ids: String?): List<String>? {
        return ids?.split(",")?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun toString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}