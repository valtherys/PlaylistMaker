package com.practicum.playlistmaker.presentation.settings

import com.practicum.playlistmaker.domain.api.settings.UserSettingsInteractor
import com.practicum.playlistmaker.presentation.ui.settings.SettingsView

class UserSettingsPresenter(private val interactor: UserSettingsInteractor) {
    private var view: SettingsView? = null

    fun attachView(view: SettingsView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun onSetSwitcherPosition(){
        view?.setSwitcher(interactor.getSavedTheme())
    }

    fun onSwitchTheme(param: Boolean){
        interactor.switchTheme(param)
    }
}