package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.search.SearchMessagesInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.impl.history.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.player.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.search.SearchMessagesInteractorImpl
import com.practicum.playlistmaker.domain.impl.search.TracksSearchInteractorImpl
import com.practicum.playlistmaker.domain.impl.settings.UserSettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.sharing.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    factory<SearchMessagesInteractor> {
        SearchMessagesInteractorImpl(get())
    }

    factory<TracksSearchInteractor> {
        TracksSearchInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    factory<UserSettingsInteractor> {
        UserSettingsInteractorImpl(get())
    }
}