package com.practicum.playlistmaker.ui.medialibrary.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlistmaker.ui.medialibrary.adapters.MediaLibraryViewPagerAdapter
import com.practicum.playlistmaker.ui.medialibrary.view_model.MediaLibraryViewModel
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import com.practicum.playlistmaker.utils.setIndicatorMargins
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.selected_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.tabLayout.setIndicatorMargins(
            context = this,
            indicatorMargin = INDICATOR_MARGIN
        )

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    companion object {
        private const val INDICATOR_MARGIN = 16f
    }
}