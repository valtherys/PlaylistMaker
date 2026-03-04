package com.practicum.playlistmaker.ui.playlist_creation.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.snackbar.CustomSnackbar
import com.practicum.playlistmaker.ui.playlist_creation.view_model.CoverUri
import com.practicum.playlistmaker.ui.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.utils.loadImage
import com.practicum.playlistmaker.utils.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

open class PlaylistCreationFragment() : BindingFragment<FragmentPlaylistCreationBinding>() {
    override val applyImeInset = true

    protected val playlistName: String
        get() = binding.etPlaylistName.text.toString()
    protected val playlistDescription: String
        get() = binding.etPlaylistDescription.text.toString()

    private val viewModel: PlaylistCreationViewModel by viewModel()

    protected lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val navController by lazy { findNavController() }
    val playlistCornerRadiusPx by lazy { requireContext().dpToPx(PLAYLIST_CORNER_RADIUS) }

    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistCreationBinding {
        return FragmentPlaylistCreationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPickMedia()
        initFragment()
        initListeners()
        observeViewModel()
    }

    protected fun initPickMedia() {
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistCover.loadImage(uri.toString(), playlistCornerRadiusPx)
                    viewModel.onCoverSelected(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
    }

    protected open fun initFragment() {
        binding.buttonCreate.isEnabled = false
    }

    protected fun initListeners() {
        binding.btnBack.setOnClickListener {
            handleBackAction()
        }

        binding.etPlaylistName.doAfterTextChanged { text ->
            binding.buttonCreate.isEnabled = text?.isNotBlank() ?: false
        }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCreate.setOnClickListener {
            onUpdatePlaylist()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackAction()
                }
            }
        )
    }

    protected open fun observeViewModel() {
        viewModel.observePlaylistUpdated().observe(viewLifecycleOwner) {
            onPlaylistUpdated(it)
        }

        viewModel.observeCoverUri().observe(viewLifecycleOwner) {
            if (it is CoverUri.CoverSaved) {
                viewModel.updatePlaylist(createPlaylistObj(it.uri))
            }
        }
    }

    protected open fun onUpdatePlaylist() {
        viewModel.onSaveImageIntoStorage(playlistName)
    }

    protected open fun handleBackAction() {
        if (viewModel.cover.value != CoverUri.Uninitialized && (playlistName.isNotBlank() || playlistDescription.isNotBlank())) {
            requireContext().showDialog(
                titleRes = R.string.finish_playlist_creation,
                messageRes = R.string.unsaved_data_will_be_lost,
                neutralBtnRes = R.string.cancel,
                positiveBtnRes = R.string.finish,
                positiveAction = { navController.navigateUp() },
            )
        } else navController.navigateUp()
    }

    protected open fun createPlaylistObj(uri: Uri?): Playlist {
        return Playlist(
            playlistName = playlistName,
            playlistDescription = playlistDescription,
            coverFilePath = uri?.toString(),
            trackIds = listOf(),
            tracksAmount = 0
        )
    }

    private fun showSnackbar() {
        val text = getString(R.string.playlist_successfully_created)
        CustomSnackbar(requireActivity().findViewById<LinearLayout>(R.id.main)).show(text)
    }

    protected open fun onPlaylistUpdated(state: Boolean) {
        if (state) {
            showSnackbar()
            navController.navigateUp()
        }
    }

    companion object {
        protected const val PLAYLIST_CORNER_RADIUS = 8f
    }
}

