package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSettingsViewModel(
    private val themeInteractor: UserSettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val _isThemeDark = MutableStateFlow(themeInteractor.getSavedTheme())
    val isThemeDark: StateFlow<Boolean> = _isThemeDark.asStateFlow()

    fun onSwitchTheme(param: Boolean) {
        if (themeInteractor.getSavedTheme() != param) {
            themeInteractor.switchTheme(param)
            val newTheme = themeInteractor.getSavedTheme()
            _isThemeDark.value = newTheme
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