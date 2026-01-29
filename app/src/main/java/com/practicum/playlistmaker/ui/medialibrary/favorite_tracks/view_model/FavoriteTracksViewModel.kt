package com.practicum.playlistmaker.ui.medialibrary.favorite_tracks.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.db.FavoritesInteractor
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val interactor: FavoritesInteractor,
    private val context: Context,
) : ViewModel() {
    private val favoritesLiveData = MutableLiveData<FavoritesState>()
    fun observeFavorites(): LiveData<FavoritesState> = favoritesLiveData

    init {
        getFavoriteTracks()
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            interactor.getFavoriteTracks().collect { tracks -> processResult(tracks) }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty(context.getString(R.string.empty_selected_tracks)))
        } else renderState(FavoritesState.Content(tracks.reversed()))
    }

    private fun renderState(state: FavoritesState) {
        favoritesLiveData.postValue(state)
    }
}