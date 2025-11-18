package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.history.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchMessagesRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.UserSettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.AppConfigRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.search.SearchMessagesRepository
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository
import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(TRACKS_CLIENT))
    }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    single<TracksSearchRepository> {
        TracksSearchRepositoryImpl(get())
    }

    single<SearchMessagesRepository> {
        SearchMessagesRepositoryImpl(androidContext())
    }

    single<UserSettingsRepository> {
        UserSettingsRepositoryImpl(get(FLAG_CLIENT))
    }

    single<AppConfigRepository> {
        AppConfigRepositoryImpl(androidContext())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}