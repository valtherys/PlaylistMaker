package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.history.PrefsStorageClient
import com.practicum.playlistmaker.data.history.StorageClient
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.player.AudioPlayer
import com.practicum.playlistmaker.data.search.NetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
private const val SHARED_PREFS_FILE = "playlist_maker"
private const val TRACKS_HISTORY_KEY = "TRACKS_HISTORY"
private const val APP_THEME_KEY = "DARK_THEME"
val TRACKS_CLIENT = named("tracks")
val FLAG_CLIENT = named("flag")

val dataModule = module {
    factory { Gson() }

    single<ITunesApiService> {
        Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    }

    single<StorageClient<List<TrackDto>>>(TRACKS_CLIENT) {
        PrefsStorageClient(
            prefs = get(),
            dataKey = TRACKS_HISTORY_KEY,
            type = object : TypeToken<List<TrackDto>>() {}.type,
            gson = get()
        )
    }

    single<StorageClient<Boolean>>(FLAG_CLIENT) {
        PrefsStorageClient(
            prefs = get(),
            dataKey = APP_THEME_KEY,
            type = Boolean::class.java,
            gson = get()
        )
    }

    factory { MediaPlayer() }

    factory { AudioPlayer(get()) }
}
