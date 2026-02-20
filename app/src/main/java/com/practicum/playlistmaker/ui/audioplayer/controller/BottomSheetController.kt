package com.practicum.playlistmaker.ui.audioplayer.controller

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetController(
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
    private val dimView: View
) {
    fun init() {

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when (slideOffset) {
                    SLIDE_OFFSET_HIDDEN -> dimView.isVisible = false
                    in (SLIDE_OFFSET_HIDDEN..SLIDE_OFFSET_COLLAPSED) -> dimView.alpha =
                        LINEAR_FUN_K * slideOffset + LINEAR_FUN_B
                    else -> dimView.alpha = DIM_VIEW_DEFAULT_ALPHA
                }
            }
        })
    }

    fun toggleBottomSheet(state: Boolean) {
        if (state) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            dimView.isVisible = true
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    companion object {
        private const val SLIDE_OFFSET_HIDDEN = -1f
        private const val SLIDE_OFFSET_COLLAPSED = 0f
        private const val LINEAR_FUN_K = 0.5f
        private const val LINEAR_FUN_B = 0.5f
        private const val DIM_VIEW_DEFAULT_ALPHA = 0.5f
    }
}