package com.practicum.playlistmaker.domain.impl.sharing

import android.R
import com.practicum.playlistmaker.domain.api.db.PlaylistsRepository
import com.practicum.playlistmaker.domain.api.sharing.AppConfigRepository
import com.practicum.playlistmaker.domain.api.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.api.sharing.PlaylistMessageBuilderRepository
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.models.EmailData
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.first

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val appConfigRepository: AppConfigRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playlistMessageBuilderRepository: PlaylistMessageBuilderRepository,
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

    override suspend fun sharePlaylist(playlistId: Int, trackIds: List<String>) {
        val playlist = playlistsRepository.getPlaylist(playlistId).first()
        val tracks = playlistsRepository.getPlaylistTracks(trackIds).first()
        val message = getPlaylistShareMessage(playlist, tracks)

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

    private fun getPlaylistShareMessage(playlist: Playlist, tracks: List<Track>): String{
        return playlistMessageBuilderRepository.buildMessage(playlist, tracks)
    }
}