package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    val userSettingsInteractor = Creator.provideUserSettingsInteractor()
                    val sharingInteractor = Creator.provideSharingInteractor()
                    UserSettingsViewModel(userSettingsInteractor, sharingInteractor)
                }
            }
        }
    }
}