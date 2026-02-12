package com.practicum.playlistmaker.ui.playlist_creation.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.playlist_creation.view_model.PlaylistCreationViewModel
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

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
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { _, _ ->
            }.setPositiveButton("Завершить") { _, _ ->
                onCreatePlaylist()
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

        viewModel.observeToastFlag().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Плейлист успешно создан", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }
    }


    private fun onCreatePlaylist() {
        coverUriSelected?.let {
            saveImageToPrivateStorage(it, playlistName)
        }

        viewModel.createPlaylist(
            createPlaylistObj()
        )
    }

    private fun handleBackAction() {
        if (coverUriSelected != null && playlistName.isNotBlank()) {
            confirmDialog.show()
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

    private fun saveImageToPrivateStorage(uri: Uri, playlistName: String) {

        val filePath = File(requireContext().filesDir, COVERS_FOLDER_NAME)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val coverFile = File(filePath, "${playlistName}_${System.currentTimeMillis()}.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(coverFile).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            }
            coverSaved = coverFile
        } ?: Log.e("SaveImage", "Failed to open input stream")
    }

    private fun loadCover(uri: Uri) {
        val playlistCornerRadiusPx = requireContext().dpToPx(PLAYLIST_CORNER_RADIUS)
        Glide.with(requireContext()).load(uri)
            .transform(CenterCrop(), RoundedCorners(playlistCornerRadiusPx))
            .into(binding.playlistCover)
    }

    companion object {
        private const val IMAGE_QUALITY = 100
        private const val COVERS_FOLDER_NAME = "playlist_covers"
        private const val PLAYLIST_CORNER_RADIUS = 8f
    }
}

