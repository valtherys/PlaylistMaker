package com.practicum.playlistmaker.domain.api.settings

interface UserSettingsInteractor {
    fun getSavedTheme(): Boolean

    fun applySavedTheme()

    fun switchTheme(darkThemeEnabled: Boolean)
}