package com.practicum.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: UserSettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.setOnCheckedChangeListener { themeSwitcher, checked ->
            viewModel.onSwitchTheme(checked)
        }

        viewModel.observeThemeValue().observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        binding.btnShare.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.btnSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.btnAgreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }
}