package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor

class UserSettingsViewModel(
    private val themeInteractor: UserSettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val isThemeDarkLiveData = MutableLiveData(themeInteractor.getSavedTheme())
    fun observeThemeValue(): LiveData<Boolean> = isThemeDarkLiveData

    fun onSwitchTheme(param: Boolean) {
        if (themeInteractor.getSavedTheme() != param) {
            themeInteractor.switchTheme(param)
            val newTheme = themeInteractor.getSavedTheme()
            isThemeDarkLiveData.postValue(newTheme)
        }
    }

    fun onShareClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onAgreementClicked() {
        sharingInteractor.openTerms()
    }
}