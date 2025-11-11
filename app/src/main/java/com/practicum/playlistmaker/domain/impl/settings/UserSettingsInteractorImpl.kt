package com.practicum.playlistmaker.domain.impl.settings

import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository

class UserSettingsInteractorImpl(private val repository: UserSettingsRepository) :
    UserSettingsInteractor {
    override fun getSavedTheme() = repository.getSavedTheme()


    override fun applySavedTheme() {
        repository.applySavedTheme()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }
}