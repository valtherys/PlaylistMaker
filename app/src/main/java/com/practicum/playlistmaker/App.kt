package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE)
        userSettingsInteractor = Creator.provideUserSettingsInteractor()

        userSettingsInteractor.applySavedTheme()
    }

    companion object {
        lateinit var userSettingsInteractor: UserSettingsInteractor
        lateinit var sharedPrefs: SharedPreferences
            private set
        const val SHARED_PREFS_FILE = "playlist_maker"
    }
}