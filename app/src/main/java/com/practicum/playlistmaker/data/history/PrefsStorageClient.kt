package com.practicum.playlistmaker.data.history

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    private val prefs: SharedPreferences,
    private val dataKey: String,
    private val type: Type,
    private val gson: Gson
) : StorageClient<T> {

    override fun storeData(data: T) {
        when (type) {
            java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> prefs.edit {
                putBoolean(
                    dataKey,
                    data as Boolean
                )
            }

            else -> {
                try {
                    prefs.edit { putString(dataKey, gson.toJson(data, type)) }
                } catch (e: Exception) {
                    Log.e("PrefsStorageClient", "Failed to store type $type, $e")
                }
            }
        }
    }

    override fun getData(): T? {
        return when (type) {
            java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> prefs.getBoolean(
                dataKey,
                false
            ) as T

            else -> {
                try {
                    val dataJson = prefs.getString(dataKey, null)
                    return if (dataJson == null) {
                        null
                    } else {
                        gson.fromJson(dataJson, type)
                    }
                } catch (e: Exception) {
                    Log.e("PrefsStorageClient", "Failed to get from store type $type, $e")
                    return null
                }
            }
        }
    }

    override fun clearData() {
        prefs.edit { remove(dataKey) }
    }
}
