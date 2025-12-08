package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics).toInt()
}

fun TabLayout.setIndicatorMargins(context: Context, indicatorMargin: Float){
    val tabStrip = this.getChildAt(0) as ViewGroup

    for (i in 0 until tabStrip.childCount) {
        val tabView = tabStrip.getChildAt(i)
        val params = tabView.layoutParams as ViewGroup.MarginLayoutParams
        if (i == 0) {
            params.marginEnd = context.dpToPx(indicatorMargin)
        } else {
            params.marginStart = context.dpToPx(indicatorMargin)
        }
        tabView.layoutParams = params
    }
}