package com.practicum.playlistmaker.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_library)

        val root = findViewById<ConstraintLayout>(R.id.main)
        root.applySystemBarsPadding()
    }
}