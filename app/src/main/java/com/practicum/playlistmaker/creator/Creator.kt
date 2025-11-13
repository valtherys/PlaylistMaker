package com.practicum.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.history.PrefsStorageClient
import com.practicum.playlistmaker.data.history.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.player.AudioPlayer
import com.practicum.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchMessagesRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.UserSettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.AppConfigRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesRepository
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository
import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.impl.player.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.search.SearchMessagesInteractorImpl
import com.practicum.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import com.practicum.playlistmaker.domain.impl.history.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.search.TracksSearchInteractorImpl
import com.practicum.playlistmaker.domain.impl.settings.UserSettingsInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var appContext: Context
    private const val TRACKS_HISTORY_KEY = "TRACKS_HISTORY"
    private const val APP_THEME_KEY = "DARK_THEME"
    private const val ITUNES_BASE_URL = "https://itunes.apple.com/"

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(ITUNES_BASE_URL).addConverterFactory(
            GsonConverterFactory.create()
        ).build()
    }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getITunesApiService(): ITunesApiService {
        return retrofit.create(ITunesApiService::class.java)
    }

    private fun getTracksRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient(getITunesApiService()))
    }

    fun provideTracksInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }

    private fun getSearchMessagesRepository(): SearchMessagesRepository {
        return SearchMessagesRepositoryImpl(appContext)
    }

    fun provideSearchMessagesInteractor(): SearchMessagesInteractor {
        return SearchMessagesInteractorImpl(getSearchMessagesRepository())
    }

    private fun getTracksHistoryRepository(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(
            PrefsStorageClient(
                appContext,
                TRACKS_HISTORY_KEY,
                object : TypeToken<List<TrackDto>>() {}.type
            )
        )
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository())
    }

    private fun getUserSettingsRepository(): UserSettingsRepository {
        return UserSettingsRepositoryImpl(
            PrefsStorageClient(
                appContext,
                APP_THEME_KEY,
                object : TypeToken<Boolean>() {}.type
            )
        )
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(appContext)
    }

    private fun getAppConfigRepository(): AppConfigRepository {
        return AppConfigRepositoryImpl(appContext)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getAppConfigRepository())
    }

    fun provideUserSettingsInteractor(): UserSettingsInteractor {
        return UserSettingsInteractorImpl(getUserSettingsRepository())
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(AudioPlayer(getMediaPlayer()))
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}