package com.practicum.playlistmaker.domain.api.sharing

import com.practicum.playlistmaker.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(sendData: EmailData, message: String)
}