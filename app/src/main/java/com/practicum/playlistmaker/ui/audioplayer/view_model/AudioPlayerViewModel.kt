package com.practicum.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.db.FavoritesInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.api.player.AudioPlayerInteractor
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val dbInteractor: FavoritesInteractor,
    private val dateFormatter: SimpleDateFormat,
    private val previewUrl: String,
    private val trackId: String,
) : ViewModel(), AudioPlayerEventListener {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        interactor.setStateListener(this)
        interactor.preparePlayer(previewUrl)
        checkTrackIsFavorite(trackId)
        playerStateLiveData.value = PlayerState.TimeProgress(dateFormatter.format(INITIAL_PROGRESS))
    }

    override fun onPlayerPrepared() {
        playerStateLiveData.postValue(PlayerState.Prepared)
    }

    override fun onPlayerStart() {
        playerStateLiveData.postValue(PlayerState.Playing)
    }

    override fun onPlayerCompletion() {
        playerStateLiveData.postValue(PlayerState.Complete)
    }

    override fun onPlayerChangePosition(position: Int) {
        playerStateLiveData.value = PlayerState.TimeProgress(dateFormatter.format(position))
    }

    override fun onPlayerPause() {
        playerStateLiveData.postValue(PlayerState.Paused)
    }

    fun onPlayClicked() {
        interactor.playbackControl()
    }

    fun onPause() {
        interactor.pausePlayer()
    }

    fun onFavoriteClicked(track: TrackParcelable) {
        viewModelScope.launch {
            if (track.isFavorite) {
                dbInteractor.deleteTrackFromFavorites(track)
            } else dbInteractor.addTrackToFavorites(track)
        }

        playerStateLiveData.postValue(PlayerState.Favorite(!track.isFavorite))
    }

    fun checkTrackIsFavorite(trackId: String) {
        viewModelScope.launch {
            dbInteractor.checkTrackIsFavorite(trackId).collect { id ->
                playerStateLiveData.postValue(
                    PlayerState.Favorite(
                        !id.isNullOrEmpty()
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        interactor.setStateListener(null)
        interactor.onRelease()
    }

    companion object{
        private const val INITIAL_PROGRESS = 0
    }
}