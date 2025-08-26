package com.practicum.playlistmaker.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.Locale

class TrackTimeAdapter : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String? {
        val millis = json.asLong
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60

        return String.format(Locale.US,"%02d:%02d", minutes, seconds)
    }
}