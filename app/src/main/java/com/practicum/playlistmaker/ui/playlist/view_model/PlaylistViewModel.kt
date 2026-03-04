package com.practicum.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.UIText
import com.practicum.playlistmaker.utils.toMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.round

class PlaylistViewModel(
    private val interactor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val playlistId: Int
) : ViewModel() {

    private val _playlistState = MutableLiveData(PlaylistState())
    fun observePlaylistState(): LiveData<PlaylistState> = _playlistState
    private val _bottomSheetIsVisible = MutableLiveData(false)
    fun observeBottomSheetState(): LiveData<Boolean> = _bottomSheetIsVisible

    private val _deletePlaylistSuccessful = MutableLiveData(false)
    fun observeDeletePlaylistFlag(): LiveData<Boolean> = _deletePlaylistSuccessful
    private var playlistJob: Job? = null
    private var tracksJob: Job? = null


    fun initPlaylist() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            interactor.getPlaylist(playlistId).collect { playlist ->
                _playlistState.value = _playlistState.value?.copy(playlist = playlist)
                playlist.trackIds?.let {
                    initTracks()
                }
            }
        }
    }

    private fun initTracks() {
        tracksJob?.cancel()
        tracksJob = viewModelScope.launch {
            interactor.getPlaylistTracks(playlistId).collect { tracks ->
                if (tracks.isNotEmpty()) {
                    val duration = calculatePlaylistDurationMinutes(tracks)
                    val tracksIdOrderMap = _playlistState.value?.playlist?.trackIds?.withIndex()
                        ?.associate { it.value to it.index }
                    val sortedTracks =
                        tracks.sortedBy { tracksIdOrderMap?.get(it.trackId) ?: Int.MAX_VALUE }
                            .reversed()
                    _playlistState.value = _playlistState.value?.copy(
                        tracksState = PlaylistTracksState.Content(sortedTracks),
                        duration = duration
                    )
                } else {
                    _playlistState.value = _playlistState.value?.copy(
                        tracksState = PlaylistTracksState.Empty,
                        duration = UIText.Plural(R.plurals.minutes_count, 0)
                    )
                }
            }
        }
    }

    fun deleteTrack(trackId: String) {
        viewModelScope.launch {
            interactor.deleteTrackFromPlaylist(trackId, playlistId)
        }
    }

    fun sharePlaylist(tracksAmountString: String) {
        when (val state = _playlistState.value?.tracksState) {
            is PlaylistTracksState.Content -> viewModelScope.launch {
                _playlistState.value?.playlist?.let {
                    sharingInteractor.sharePlaylist(
                        tracksAmountString,
                        it,
                        state.tracks
                    )
                }
            }

            else -> {}
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val res = interactor.deletePlaylist(playlistId)
            if (res) {
                _deletePlaylistSuccessful.value = true
            }
        }
    }

    fun hideBottomSheet() {
        _bottomSheetIsVisible.value = false
    }

    fun openBottomSheet() {
        _bottomSheetIsVisible.value = true
    }

    private fun calculatePlaylistDurationMinutes(tracks: List<Track>): UIText.Plural {
        val tracksDurationMillis = tracks.mapNotNull { track -> track.trackTime?.toMillis() }
        val durationMillis = tracksDurationMillis.sum()
        val durationMinutes = round(durationMillis / MILLIS_IN_MINUTE).toInt()
        return UIText.Plural(R.plurals.minutes_count, durationMinutes)
    }

    companion object {
        private const val MILLIS_IN_MINUTE = 60000.0
    }
}