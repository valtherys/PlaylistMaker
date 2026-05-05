package com.practicum.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.ui.settings.screen.SettingsScreen
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import com.practicum.playlistmaker.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: UserSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}