package com.practicum.playlistmaker.data.sharing

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.models.EmailData

class AppConfigRepositoryImpl(val context: Context) : AppConfigRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.course_link)
    }

    override fun getSupportEmailData(): EmailData {
        val email = context.getString(R.string.letter_address)
        val subject = context.getString(R.string.letter_subject)
        val text = context.getString(R.string.letter_text)
        return EmailData(
            text = text,
            subject = subject,
            email = email
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_link)
    }

    override fun getMessageToUser(): String {
        return context.getString(R.string.choose_mail_app)
    }
}