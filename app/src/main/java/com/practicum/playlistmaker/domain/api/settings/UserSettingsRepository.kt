package com.practicum.playlistmaker.domain.api.settings

interface UserSettingsRepository {
    fun getSavedTheme(): Boolean
    fun applySavedTheme()

    fun switchTheme(param: Boolean)
}