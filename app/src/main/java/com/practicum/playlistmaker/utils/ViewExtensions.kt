package com.practicum.playlistmaker.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText

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

fun View.applySystemBarsPaddingExceptBottom() {
    val initialPaddingStart = paddingStart
    val initialPaddingTop = paddingTop
    val initialPaddingEnd = paddingEnd

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPaddingRelative(
            initialPaddingStart + systemBars.left,
            initialPaddingTop + systemBars.top,
            initialPaddingEnd + systemBars.right,
            0
        )
        insets
    }
}

fun BottomNavigationView.applyBottomNavViewPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPaddingRelative(
            0,
            0,
            0,
            systemBars.bottom
        )
        insets
    }
}

fun TabLayout.setIndicatorMargins(indicatorMarginPx: Int) {
    val tabStrip = this.getChildAt(0) as ViewGroup

    for (i in 0 until tabStrip.childCount) {
        val tabView = tabStrip.getChildAt(i)
        val params = tabView.layoutParams as ViewGroup.MarginLayoutParams
        if (i == 0) {
            params.marginEnd = indicatorMarginPx
        } else {
            params.marginStart = indicatorMarginPx
        }
        tabView.layoutParams = params
    }
}

fun View.applySystemBarsImeInsets() {
    val originalPaddingTop = paddingTop
    val originalPaddingBottom = paddingBottom
    val originalPaddingStart = paddingStart
    val originalPaddingEnd = paddingEnd

    var insetBottom = 0

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        insetBottom = if (ime.bottom > 0) {
            originalPaddingBottom + ime.bottom
        } else {
            originalPaddingBottom + systemBars.bottom
        }

        view.setPaddingRelative(
            originalPaddingStart + systemBars.left,
            originalPaddingTop + systemBars.top,
            originalPaddingEnd + systemBars.right,
            insetBottom
        )

        insets
    }

    viewTreeObserver.addOnGlobalLayoutListener {
        val focused = findFocus()
        if (focused is TextInputEditText) {
            post {
                scrollTo(0, focused.bottom + insetBottom)
            }
        }
    }
}
