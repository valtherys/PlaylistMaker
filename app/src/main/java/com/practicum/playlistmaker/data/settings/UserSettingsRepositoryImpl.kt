package com.practicum.playlistmaker.data.settings

import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository

class UserSettingsRepositoryImpl(private val themeSwitcher: ThemeSwitcher) :
    UserSettingsRepository {
    override fun getSavedTheme() = themeSwitcher.getSavedTheme()


    override fun applySavedTheme() {
        themeSwitcher.applySavedTheme()
    }

    override fun switchTheme(param: Boolean) {
        themeSwitcher.switchTheme(param)
    }
}