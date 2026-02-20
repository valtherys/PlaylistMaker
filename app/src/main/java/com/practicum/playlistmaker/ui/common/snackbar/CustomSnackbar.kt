package com.practicum.playlistmaker.ui.common.snackbar

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

import com.practicum.playlistmaker.R


class CustomSnackbar(private val activityRoot: View) {
    fun show(message: String){
        val snackbar = Snackbar.make(activityRoot, " ", Snackbar.LENGTH_SHORT)
        val layout = LayoutInflater.from(activityRoot.context).inflate(R.layout.snackbar_custom, null)
        val textView = layout.findViewById<TextView>(R.id.tv_snackbar_text)
        textView.text = message

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(layout)

        snackbar.show()
    }
}