package com.practicum.playlistmaker.ui.playlist_creation.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.snackbar.CustomSnackbar
import com.practicum.playlistmaker.ui.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistCreationFragment() : BindingFragment<FragmentPlaylistCreationBinding>() {
    override val applyImeInset = true
    private var coverUriSelected: Uri? = null
    private var coverSaved: File? = null

    private val playlistName: String
        get() = binding.etPlaylistName.text.toString()
    private val playlistDescription: String
        get() = binding.etPlaylistDescription.text.toString()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel: PlaylistCreationViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistCreationBinding {
        return FragmentPlaylistCreationBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setNeutralButton(R.string.cancel) { _, _ ->
            }.setPositiveButton(R.string.finish) { _, _ ->
                findNavController().navigateUp()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreate.isEnabled = false

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    loadCover(uri)
                    coverUriSelected = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

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
            onCreatePlaylist()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackAction()
                }
            }
        )

        viewModel.observePlaylistCreationFlag().observe(viewLifecycleOwner) {
            if (it) {
                showSnackbar()
                findNavController().navigateUp()
            }
        }

        viewModel.observeSavedCover().observe(viewLifecycleOwner){
            it?.let {
                coverSaved = it
                viewModel.createPlaylist(createPlaylistObj())
            }
        }
    }

    private fun onCreatePlaylist() {
        coverUriSelected?.let {
            viewModel.saveImageIntoStorage(it, playlistName)
            return
        }

        viewModel.createPlaylist(
            createPlaylistObj()
        )
    }

    private fun handleBackAction() {
        if (coverUriSelected != null && (playlistName.isNotBlank() || playlistDescription.isNotBlank())) {
            val dialog = confirmDialog.show()
            stileDialog(dialog)
        } else findNavController().navigateUp()
    }

    private fun createPlaylistObj(): Playlist {
        return Playlist(
            playlistName = playlistName,
            playlistDescription = playlistDescription,
            coverFilePath = coverSaved?.toUri().toString(),
            trackIds = listOf(),
            tracksAmount = 0
        )
    }


    private fun loadCover(uri: Uri) {
        val playlistCornerRadiusPx = requireContext().dpToPx(PLAYLIST_CORNER_RADIUS)
        Glide.with(requireContext()).load(uri)
            .transform(CenterCrop(), RoundedCorners(playlistCornerRadiusPx))
            .into(binding.playlistCover)
    }

    private fun showSnackbar() {
        val text = getString(R.string.playlist_successfully_created)
        CustomSnackbar(requireActivity().findViewById<LinearLayout>(R.id.main)).show(text)
    }

    private fun stileDialog(dialog: AlertDialog){
        val buttonPadding = requireContext().dpToPx(BUTTON_PADDING_HORIZONTAL)
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL)
            .setTextColor(getColor(requireContext(), R.color.YP_Blue))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setPadding(buttonPadding,BUTTON_PADDING_VERTICAL, buttonPadding,BUTTON_PADDING_VERTICAL)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(requireContext(), R.color.YP_Blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setPadding(buttonPadding,BUTTON_PADDING_VERTICAL, buttonPadding,BUTTON_PADDING_VERTICAL)
    }

    companion object {
        private const val PLAYLIST_CORNER_RADIUS = 8f
        private const val BUTTON_PADDING_HORIZONTAL = 16f
        private const val BUTTON_PADDING_VERTICAL = 0
    }
}

