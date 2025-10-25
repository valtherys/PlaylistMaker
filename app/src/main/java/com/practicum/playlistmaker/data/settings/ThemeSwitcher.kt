package com.practicum.playlistmaker.data.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.Creator

class ThemeSwitcher(private val sharedPrefs: SharedPreferences) {
    var darkTheme = false
        private set

    fun getSavedTheme(): Boolean {
        darkTheme = sharedPrefs.getBoolean(APP_THEME_KEY, false)
        return darkTheme
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        Creator.sharedPrefs.edit { putBoolean(APP_THEME_KEY, darkThemeEnabled) }

        setTheme(darkThemeEnabled)
    }

    fun applySavedTheme(){
        getSavedTheme()
        setTheme(darkTheme)
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

    companion object{
        private const val APP_THEME_KEY = "DARK_THEME"
    }
}