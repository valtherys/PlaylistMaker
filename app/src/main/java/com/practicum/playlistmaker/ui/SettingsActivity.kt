package com.practicum.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        val root = findViewById<LinearLayout>(R.id.main)
        root.applySystemBarsPadding()

        val btnBack = findViewById<TextView>(R.id.btn_back)
        val btnShare = findViewById<FrameLayout>(R.id.btn_share)
        val btnSupport = findViewById<FrameLayout>(R.id.btn_support)
        val btnAgreement = findViewById<FrameLayout>(R.id.btn_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val app = application as App

        themeSwitcher.isChecked = app.darkTheme

        themeSwitcher.setOnCheckedChangeListener { themeSwitcher, checked ->
            (applicationContext as App).switchTheme(
                checked
            )
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_link)))
        }

        btnSupport.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.letter_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.letter_text))
            }

            startActivity(Intent.createChooser(sendIntent, getString(R.string.choose_mail_app)))
        }

        btnAgreement.setOnClickListener {
            val urlIntent =
                Intent(Intent.ACTION_VIEW, getString(R.string.agreement_link).toUri())
            startActivity(urlIntent)
        }
    }
}