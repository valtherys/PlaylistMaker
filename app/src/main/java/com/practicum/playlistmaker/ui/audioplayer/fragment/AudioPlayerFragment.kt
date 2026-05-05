package com.practicum.playlistmaker.ui.audioplayer.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.services.MusicService
import com.practicum.playlistmaker.ui.audioplayer.adapters.PlaylistsBottomSheetAdapter
import com.practicum.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.practicum.playlistmaker.ui.audioplayer.view_model.PlaylistsState
import com.practicum.playlistmaker.ui.audioplayer.view_model.TrackEvents
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.common.controller.BottomSheetController
import com.practicum.playlistmaker.ui.common.snackbar.CustomSnackbar
import com.practicum.playlistmaker.ui.models.TrackParcelable
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.utils.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.text.isNullOrEmpty

class AudioPlayerFragment : BindingFragment<FragmentAudioPlayerBinding>() {
    private var track: TrackParcelable? = null
    private lateinit var playlistsAdapter: PlaylistsBottomSheetAdapter
    private val albumCornerRadiusDp: Float = ALBUM_CORNER_RADIUS_DP
    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(
            track,
        )
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetController: BottomSheetController
    private var musicService: MusicService? = null
    private var foregroundIsInitialized = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            viewModel.setAudioPlayerEventListener(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }
    private lateinit var intent: Intent
    private var isPlaying = false
    private var isPlayerPrepared = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAudioPlayerBinding {
        return FragmentAudioPlayerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = arguments?.getParcelable(ARG_TRACK, TrackParcelable::class.java)
        intent = Intent(requireActivity(), MusicService::class.java).apply {
            putExtra(MusicService.ARG_TRACK, track)
        }
    }

    override fun onStart() {
        super.onStart()
        updateButtonPlayIsEnabled()
        viewModel.onFragmentStart()
    }

    override fun onResume() {
        super.onResume()
        updateButtonPlayIsEnabled()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            initializeForeground()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isPlaying) {
            viewModel.onFragmentStop()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindMusicService()
        requireContext().stopService(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumCornerRadiusPx = requireContext().dpToPx(albumCornerRadiusDp)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks)
        bottomSheetController = BottomSheetController(bottomSheetBehavior, binding.dimView)
        bottomSheetController.init()

        playlistsAdapter =
            PlaylistsBottomSheetAdapter { playlist -> viewModel.onPlaylistClicked(playlist) }
        binding.playlists.adapter = playlistsAdapter
        binding.playlists.layoutManager = LinearLayoutManager(requireContext())

        track?.let {
            bindData(it, albumCornerRadiusPx)
        }
        bindMusicService()

        binding.btnPlay.setOnClickListener {
            handlePlayClick()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddToFavorites.setOnClickListener {
            viewModel.onFavoriteClicked(track!!)
        }

        binding.btnAddToLibrary.setOnClickListener {
            onAddToPlaylistClicked()
        }

        binding.dimView.setOnClickListener {
            viewModel.hideBottomSheet()
        }

        binding.btnCreateNewPlaylist.setOnClickListener {
            viewModel.hideBottomSheet()
            findNavController().navigate(R.id.action_audioPlayerFragment_to_playlistCreationFragment)
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) { renderPlayer(it) }
        viewModel.playlistsStateLiveData.observe(viewLifecycleOwner) { renderPlaylists(it) }
        viewModel.observeTrackEvents().observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                processEvents(it)
            }
        }
        viewModel.observeBottomSheetState()
            .observe(viewLifecycleOwner) { bottomSheetController.toggleBottomSheet(it) }
        viewModel.checkTrackIsFavorite(track?.trackId!!)
    }

    private fun bindData(track: TrackParcelable, cornerRadiusPx: Int) {
        val albumCoverHighResolution = track.getArtworkUrlHighResolution()
        Glide.with(this)
            .load(albumCoverHighResolution)
            .placeholder(R.drawable.ic_placeholder_45)
            .transform(RoundedCorners(cornerRadiusPx)).into(binding.ivAlbumCover)

        binding.apply {
            tvTrackName.text = track.trackName
            tvTrackArtist.text = track.artistName
            tvDurationData.text = track.trackTime
            tvGenreData.text = track.primaryGenreName
            tvCountryData.text = track.country
        }

        hideViewGroupIfEmpty(track.collectionName, binding.groupAlbum, binding.tvAlbumData)
        hideViewGroupIfEmpty(track.getReleaseYear(), binding.groupYear, binding.tvYearData)
        setFavoriteBtnImg(track.isFavorite)
    }

    private fun hideViewGroupIfEmpty(
        field: String?,
        viewGroup: Group,
        dataField: TextView
    ) {
        if (field.isNullOrEmpty()) {
            viewGroup.visibility = View.GONE
        } else {
            viewGroup.visibility = View.VISIBLE
            dataField.text = field
        }
    }

    fun onPlayerChangePosition(position: String) {
        binding.tvTimer.text = position
    }

    fun onPlayerPrepared() {
        isPlayerPrepared = true
        updateButtonPlayIsEnabled()
    }

    fun onPlayerCompletion() {
        binding.apply {
            btnPlay.toggleBtn(true)
            tvTimer.text = getString(R.string.count_start)
            isPlaying = false
        }
    }

    private fun setFavoriteBtnImg(isFavorite: Boolean) {
        binding.btnAddToFavorites.setImageResource(
            if (isFavorite) {
                R.drawable.ic_remove_from_favorites_51
            } else {
                R.drawable.ic_add_to_favorites_51
            }
        )
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistsAdapter.submitList(playlists)
    }

    private fun showEmptyState() {
        playlistsAdapter.submitList(listOf())
    }

    private fun showSnackbar(playlistName: String, messageResource: Int) {
        val text = requireContext().getString(messageResource).format(playlistName)
        CustomSnackbar(requireActivity().findViewById<LinearLayout>(R.id.main)).show(text)
    }

    private fun onAddToPlaylistClicked() {
        viewModel.openBottomSheet()
    }

    private fun handleTrackAdded(playlistName: String, messageResource: Int) {
        viewModel.hideBottomSheet()
        showSnackbar(playlistName, messageResource)
    }

    private fun renderPlayer(state: PlayerState) {
        when (state) {
            PlayerState.Default -> updateButtonPlayIsEnabled()
            PlayerState.Prepared -> onPlayerPrepared()
            PlayerState.Complete -> onPlayerCompletion()
            is PlayerState.TimeProgress -> onPlayerChangePosition(state.progress)
            is PlayerState.Favorite -> {
                track?.isFavorite = state.isFavorite
                setFavoriteBtnImg(state.isFavorite)
            }
        }
    }

    private fun renderPlaylists(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showEmptyState()
            is PlaylistsState.Playlists -> {
                showPlaylists(state.playlists)
            }
        }
    }

    private fun processEvents(state: TrackEvents) {
        when (state) {
            is TrackEvents.TrackAdded -> handleTrackAdded(state.playlistName, state.message)
            is TrackEvents.TrackAlreadyAdded -> showSnackbar(state.playlistName, state.message)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showRationalDialog()
            } else updateButtonPlayIsEnabled()
        }

    private fun bindMusicService() {
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    private fun askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PERMISSION_GRANTED
    }

    private fun initializeForeground() {
        if (!foregroundIsInitialized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkPermission(requireContext())) {
                    ContextCompat.startForegroundService(requireContext(), intent)
                }
            } else {
                ContextCompat.startForegroundService(requireContext(), intent)
            }
            foregroundIsInitialized = true
        }
    }

    private fun handlePlayClick() {
        when {
            checkPermission(requireContext()) -> {
                viewModel.onPlayClicked()
                isPlaying = !isPlaying
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showRationalDialog()
            }

            else -> {
                askPermission()
            }
        }
    }

    private fun showRationalDialog() {
        requireContext().showDialog(
            titleRes = R.string.permission_required,
            messageRes = R.string.go_to_settings,
            positiveBtnRes = R.string.yes,
            negativeBtnRes = R.string.no,
            positiveAction = { openAppSettings() },
            negativeAction = {},
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun updateButtonPlayIsEnabled() {
        binding.btnPlay.isEnabled = isPlayerPrepared && checkPermission(requireContext())
    }

    companion object {
        const val ARG_TRACK = "TRACK"
        private const val ALBUM_CORNER_RADIUS_DP = 8f
        fun createArgs(track: TrackParcelable): Bundle =
            bundleOf(ARG_TRACK to track)
    }
}