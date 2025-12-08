package com.practicum.playlistmaker.ui.medialibrary.selected_tracks.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentSelectedTracksBinding
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.medialibrary.selected_tracks.view_model.SelectedTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SelectedTracksFragment : BindingFragment<FragmentSelectedTracksBinding>() {
    private val viewmodel: SelectedTracksViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectedTracksBinding {
        return FragmentSelectedTracksBinding.inflate(inflater, container, false)
    }

    companion object{
        fun newInstance() = SelectedTracksFragment().apply { }
    }
}