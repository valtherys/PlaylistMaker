package com.practicum.playlistmaker.ui.audioplayer.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.practicum.playlistmaker.R

internal class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = R.style.DefPlaybackButtonViewStyle,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var isPlaying = false
    private var drawable: Drawable? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr, defStyleRes
        ).apply {
            try {
                playDrawable = context.getDrawable(getResourceId(
                    R.styleable.PlaybackButtonView_btnPlay,
                    R.drawable.ic_play_100
                ))
                pauseDrawable = context.getDrawable(getResourceId(
                    R.styleable.PlaybackButtonView_btnPause,
                    R.drawable.ic_pause_100
                ))
                drawable = playDrawable
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawable?.setBounds(0, 0, width, height)
        drawable?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                return true
            }

            MotionEvent.ACTION_UP -> {
                isPressed = false
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun toggleBtn(paused: Boolean = false) {
        isPlaying = if (paused) false else !isPlaying
        drawable = if(isPlaying) pauseDrawable else playDrawable
        invalidate()
    }

    override fun performClick(): Boolean {
        toggleBtn()
        return super.performClick()
    }
}