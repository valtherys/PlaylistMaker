package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.player.AudioPlayer
import com.practicum.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.history.SearchHistory
import com.practicum.playlistmaker.data.settings.ThemeSwitcher
import com.practicum.playlistmaker.data.history.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.UserSettingsRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.history.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.history.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.search.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.search.TracksSearchRepository
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository
import com.practicum.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksSearchInteractorImpl
import com.practicum.playlistmaker.domain.impl.UserSettingsInteractorImpl
import com.practicum.playlistmaker.presentation.player.AudioPlayerPresenter
import com.practicum.playlistmaker.presentation.history.TracksHistoryPresenter
import com.practicum.playlistmaker.presentation.search.TracksSearchPresenter
import com.practicum.playlistmaker.presentation.settings.UserSettingsPresenter

object Creator {
    private fun getTracksRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getTracksInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }

    fun provideTracksPresenter(): TracksSearchPresenter {
        return TracksSearchPresenter(getTracksInteractor())
    }

    private fun getTracksHistoryRepository(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(SearchHistory(App.sharedPrefs))
    }

    fun getTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository())
    }

    fun provideTracksHistoryPresenter(): TracksHistoryPresenter{
        return TracksHistoryPresenter(getTracksHistoryInteractor())
    }

    private fun getUserSettingsRepository(): UserSettingsRepository {
        return UserSettingsRepositoryImpl(ThemeSwitcher(App.sharedPrefs))
    }

    fun provideUserSettingsInteractor(): UserSettingsInteractor {
        return UserSettingsInteractorImpl(getUserSettingsRepository())
    }
    fun provideUserSettingsPresenter(): UserSettingsPresenter{
        return UserSettingsPresenter(App.userSettingsInteractor)
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(AudioPlayer())
    }

    private fun getAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }

    fun provideAudioPlayerPresenter(): AudioPlayerPresenter{
        return AudioPlayerPresenter(getAudioPlayerInteractor())
    }

}