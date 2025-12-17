package com.practicum.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.medialibrary.adapters.MediaLibraryViewPagerAdapter
import com.practicum.playlistmaker.ui.medialibrary.view_model.MediaLibraryViewModel
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.utils.setIndicatorMargins
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : BindingFragment<FragmentMediaLibraryBinding>() {
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaLibraryBinding {
        return FragmentMediaLibraryBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.selected_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.tabLayout.setIndicatorMargins(
            indicatorMarginPx = requireContext().dpToPx(INDICATOR_MARGIN)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

    companion object {
        private const val INDICATOR_MARGIN = 16f
    }
}