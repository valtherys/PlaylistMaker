package com.practicum.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class SettingsActivity : AppCompatActivity(), SettingsView {
    private lateinit var binding: ActivitySettingsBinding
    private val userSettingsPresenter = Creator.provideUserSettingsPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        binding.themeSwitcher.setOnCheckedChangeListener { themeSwitcher, checked ->
            userSettingsPresenter.onSwitchTheme(checked)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_link)))
        }

        binding.btnSupport.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.letter_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.letter_text))
            }

            startActivity(Intent.createChooser(sendIntent, getString(R.string.choose_mail_app)))
        }

        binding.btnAgreement.setOnClickListener {
            val urlIntent =
                Intent(Intent.ACTION_VIEW, getString(R.string.agreement_link).toUri())
            startActivity(urlIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        userSettingsPresenter.attachView(this)
        userSettingsPresenter.onSetSwitcherPosition()
    }

    override fun onStop() {
        super.onStop()
        userSettingsPresenter.detachView()
    }

    override fun setSwitcher(param: Boolean){
        binding.themeSwitcher.isChecked = param
    }
}