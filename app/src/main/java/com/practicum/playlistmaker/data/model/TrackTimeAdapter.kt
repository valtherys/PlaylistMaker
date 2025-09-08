package com.practicum.playlistmaker.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.Locale

class TrackTimeAdapter : JsonDeserializer<String>, JsonSerializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String? {
        val millis = json.asLong
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60

        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    override fun serialize(
        src: String,
        typeOfSrc: Type,
        context: JsonSerializationContext?
    ): JsonElement? {
        val minutes = src.substringBefore(':').toLong() * 60 * 1000
        val seconds = src.substringAfter(":").toLong() * 1000

        return JsonPrimitive(minutes + seconds)
    }


}