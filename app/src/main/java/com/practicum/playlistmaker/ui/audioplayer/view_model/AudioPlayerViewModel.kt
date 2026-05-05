package com.practicum.playlistmaker.ui.audioplayer.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.db.FavoritesInteractor
import com.practicum.playlistmaker.domain.api.db.PlaylistsInteractor
import com.practicum.playlistmaker.domain.api.player.AudioPlayerEventListener
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.SingleEvent
import com.practicum.playlistmaker.ui.models.TrackParcelable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val tracksDbInteractor: FavoritesInteractor,
    private val playlistsDbInteractor: PlaylistsInteractor,
    private val track: TrackParcelable,
) : ViewModel() {
    private val _playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = _playerStateLiveData
    private var audioPlayerEventListener: AudioPlayerEventListener? = null

    val playlistsStateLiveData: LiveData<PlaylistsState> =
        playlistsDbInteractor.getPlaylistsFromDb()
            .map { playlists ->
                if (playlists.isNullOrEmpty()) {
                    PlaylistsState.Empty
                } else {
                    PlaylistsState.Playlists(playlists)
                }
            }
            .asLiveData()

    private val _trackEvents = MutableLiveData<SingleEvent<TrackEvents>>()
    fun observeTrackEvents(): LiveData<SingleEvent<TrackEvents>> = _trackEvents

    private val _bottomSheetIsVisible = MutableLiveData(false)
    fun observeBottomSheetState(): LiveData<Boolean> = _bottomSheetIsVisible

    init {
        checkTrackIsFavorite(track.trackId)
    }
    fun setAudioPlayerEventListener(audioPlayerEventListener: AudioPlayerEventListener) {
        this.audioPlayerEventListener = audioPlayerEventListener

        viewModelScope.launch {
            audioPlayerEventListener.providePlayerState().collect() {
                _playerStateLiveData.postValue(it)
            }
        }
    }

    fun onPlayClicked() {
        audioPlayerEventListener?.playbackControl()
    }

    fun onFavoriteClicked(track: TrackParcelable) {
        viewModelScope.launch {
            if (track.isFavorite) {
                tracksDbInteractor.deleteTrackFromFavorites(track)
            } else tracksDbInteractor.addTrackToFavorites(track)
        }

        _playerStateLiveData.postValue(PlayerState.Favorite(!track.isFavorite))
    }

    fun onFragmentStop(){
        audioPlayerEventListener?.startForeground()
    }

    fun onFragmentStart(){
        audioPlayerEventListener?.stopForeground()
    }

    fun checkTrackIsFavorite(trackId: String) {
        viewModelScope.launch {
            tracksDbInteractor.checkTrackIsFavorite(trackId).collect { id ->
                Log.d("IS_FAVORITE_ASKED", "${!id.isNullOrEmpty()}")
                _playerStateLiveData.value =
                    PlayerState.Favorite(!id.isNullOrEmpty())
                
            }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        val trackAdded = playlist.trackIds?.contains(track.trackId) ?: false
        if (trackAdded) {
            _trackEvents.value =
                SingleEvent(
                    TrackEvents.TrackAlreadyAdded(
                        playlist.playlistName,
                        R.string.track_already_added_to_playlist
                    )
                )
        } else {
            onAddTrackInPlaylist(track, playlist)
        }
    }

    fun onAddTrackInPlaylist(track: TrackParcelable, playlist: Playlist) {
        viewModelScope.launch {
            val trackInserted = playlistsDbInteractor.addTrackInPlaylist(
                track, playlist
            )
            if (trackInserted) {
                _trackEvents.value = SingleEvent(
                    TrackEvents.TrackAdded(
                        playlist.playlistName,
                        R.string.track_added_to_playlist
                    )
                )
            }
        }
    }

    fun hideBottomSheet() {
        _bottomSheetIsVisible.value = false
    }

    fun openBottomSheet() {
        _bottomSheetIsVisible.value = true
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerEventListener = null
    }
}