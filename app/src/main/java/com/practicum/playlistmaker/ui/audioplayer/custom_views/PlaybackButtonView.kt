package com.practicum.playlistmaker.ui.audioplayer.custom_views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

internal class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = R.style.DefPlaybackButtonViewStyle,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var playResId: Int
    private var pauseResId: Int
    private var isPlaying = false
    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var imageBitmap: Bitmap? = null
    private var imageRect = RectF(0f, 0f, 0f, 0f)


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr, defStyleRes
        ).apply {
            try {
                playResId = getResourceId(
                    R.styleable.PlaybackButtonView_btnPlay,
                    R.drawable.ic_play_100
                )
                pauseResId = getResourceId(
                    R.styleable.PlaybackButtonView_btnPause,
                    R.drawable.ic_pause_100
                )

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
        playBitmap = context.getDrawable(playResId)?.toBitmap(
            w,
            h
        )
        pauseBitmap = context.getDrawable(pauseResId)?.toBitmap(
            w,
            h
        )
        imageBitmap = playBitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        imageBitmap?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
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
        imageBitmap = if (isPlaying) pauseBitmap else playBitmap

        invalidate()
    }

    override fun performClick(): Boolean {
        toggleBtn()
        return super.performClick()
    }
}