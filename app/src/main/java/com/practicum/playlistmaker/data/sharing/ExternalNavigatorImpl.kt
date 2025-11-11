package com.practicum.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        context.startActivity(
            Intent.createChooser(shareIntent, link).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openEmail(sendData: EmailData, message: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, sendData.email)
            putExtra(Intent.EXTRA_SUBJECT, sendData.subject)
            putExtra(Intent.EXTRA_TEXT, sendData.text)
        }

        context.startActivity(
            Intent.createChooser(sendIntent, message).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openLink(link: String) {
        val urlIntent =
            Intent(Intent.ACTION_VIEW, link.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(urlIntent)
    }
}