package com.practicum.playlistmaker.domain.impl.sharing

import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.models.EmailData
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val appConfigRepository: AppConfigRepository,
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

    override fun sharePlaylist(
        tracksAmountString: String,
        playlist: Playlist,
        tracks: List<Track>
    ) {
        val message = getPlaylistShareMessage(tracksAmountString, playlist, tracks)

        externalNavigator.sharePlaylist(message)
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

    private fun getPlaylistShareMessage(
        tracksAmountString: String,
        playlist: Playlist,
        tracks: List<Track>
    ): String {
        return buildString {
            appendLine(playlist.playlistName)
            if (!playlist.playlistDescription.isNullOrEmpty()) {
                appendLine(playlist.playlistDescription)
            }
            appendLine(tracksAmountString)
            appendLine()
            tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
            }
        }
    }
}