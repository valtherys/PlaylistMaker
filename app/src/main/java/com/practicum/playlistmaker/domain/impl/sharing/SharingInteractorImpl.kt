package com.practicum.playlistmaker.domain.impl.sharing

import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val appConfigRepository: AppConfigRepository
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData(), getMessage())
    }

    private fun getShareAppLink(): String {
        return appConfigRepository.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return appConfigRepository.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return appConfigRepository.getTermsLink()
    }

    private fun getMessage(): String {
        return appConfigRepository.getMessageToUser()
    }
}