package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {
    var darkTheme = false
        private set
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        setTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit { putBoolean(DARK_THEME_KEY, darkThemeEnabled) }

        setTheme(darkThemeEnabled)
    }

    private fun setTheme(param: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (param) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val SHARED_PREFS_FILE = "playlist_maker"
        private const val DARK_THEME_KEY = "DARK_THEME"
    }
}