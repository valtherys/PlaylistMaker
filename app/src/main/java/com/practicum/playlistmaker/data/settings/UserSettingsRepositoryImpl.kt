package com.practicum.playlistmaker.data.settings

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.history.StorageClient
import com.practicum.playlistmaker.domain.api.settings.UserSettingsRepository

class UserSettingsRepositoryImpl(private val storage: StorageClient<Boolean>) :
    UserSettingsRepository {
    var darkTheme = false
        private set

    override fun getSavedTheme(): Boolean {
        darkTheme = storage.getData() ?: false
        return darkTheme
    }


    override fun applySavedTheme() {
        getSavedTheme()
        setTheme(darkTheme)
    }

    override fun switchTheme(param: Boolean) {
        darkTheme = param
        storage.storeData(param)

        setTheme(param)
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
}