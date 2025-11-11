package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.UserSettingsViewModel
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: UserSettingsViewModel by viewModels {
        UserSettingsViewModel.Companion.getFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()


        binding.themeSwitcher.setOnCheckedChangeListener { themeSwitcher, checked ->
            viewModel.onSwitchTheme(checked)
        }

        viewModel.observeThemeValue().observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        binding.btnBack.setOnClickListener {
            finish()
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