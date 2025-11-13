package com.practicum.playlistmaker.domain.api.sharing

import com.practicum.playlistmaker.domain.models.EmailData

interface AppConfigRepository {
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
    fun getTermsLink(): String
    fun getMessageToUser(): String
}