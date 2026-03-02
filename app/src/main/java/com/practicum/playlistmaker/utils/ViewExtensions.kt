package com.practicum.playlistmaker.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R


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

fun View.setImeInsetsWithFocus() {
    var insetBottom = 0

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        insetBottom = if (ime.bottom > 0) {
            ime.bottom
        } else 0

        view.updatePadding(bottom = ime.bottom)

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


fun ImageView.loadImage(path: String?, cornerRadPx: Int? = null) {
    val insert =
        Glide.with(this).load(path).placeholder((R.drawable.ic_placeholder_45)).transform(
            CenterCrop()
        )

    cornerRadPx?.let {
        insert.transform(CenterCrop(), RoundedCorners(cornerRadPx))
    }

    insert.into(this)
}
