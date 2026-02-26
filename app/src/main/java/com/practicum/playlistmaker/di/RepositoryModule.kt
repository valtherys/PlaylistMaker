package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.mappers.TrackDbMapper
import com.practicum.playlistmaker.data.mappers.TrackDtoMapper
import com.practicum.playlistmaker.data.db.FavoritesRepositoryImpl
import com.practicum.playlistmaker.data.db.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.history.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.mappers.PlaylistDbMapper
import com.practicum.playlistmaker.data.mappers.PlaylistTrackDbMapper
import com.practicum.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchMessagesRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.UserSettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.AppConfigRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.data.sharing.PlaylistMessageBuilderRepositoryImpl
import com.practicum.playlistmaker.data.storage.ImageStorageRepositoryImpl
import com.practicum.playlistmaker.domain.api.db.FavoritesRepository
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.image_storage.ImageStorageRepository
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.search.SearchMessagesRepository
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository
import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.api.sharing.PlaylistMessageBuilderRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(TRACKS_CLIENT), get())
    }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    factory<TracksSearchRepository> {
        TracksSearchRepositoryImpl(get(), get())
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

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory { TrackDbMapper() }
    factory { TrackDtoMapper() }
    factory { PlaylistDbMapper() }
    factory { PlaylistTrackDbMapper() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get())
    }
    factory<ImageStorageRepository> { ImageStorageRepositoryImpl(get()) }

    factory<PlaylistMessageBuilderRepository> { PlaylistMessageBuilderRepositoryImpl(androidContext()) }
}