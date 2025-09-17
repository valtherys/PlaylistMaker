package com.practicum.playlistmaker.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.applySystemBarsPadding() {
    val initialPaddingStart = paddingStart
    val initialPaddingTop = paddingTop
    val initialPaddingEnd = paddingEnd
    val initialPaddingBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPaddingRelative(
            initialPaddingStart + systemBars.left,
            initialPaddingTop + systemBars.top,
            initialPaddingEnd + systemBars.right,
            initialPaddingBottom + systemBars.bottom
        )
        insets
    }
}