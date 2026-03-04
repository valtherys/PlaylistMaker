package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.R
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val DIALOG_BUTTON_PADDING_END = 16f
private const val DIALOG_BUTTON_PADDING_START = 0
private const val DIALOG_BUTTON_PADDING_VERTICAL = 0
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    ).toInt()
}

fun Context.styleDialog(dialog: AlertDialog) {
    val buttonPadding = this.dpToPx(DIALOG_BUTTON_PADDING_END)
    (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.YP_Blue))
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.YP_Blue))
    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        .setTextColor(ContextCompat.getColor(this, R.color.YP_Blue))
    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setPadding(
        buttonPadding,
        DIALOG_BUTTON_PADDING_VERTICAL,
        buttonPadding,
        DIALOG_BUTTON_PADDING_VERTICAL
    )
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setPadding(
        DIALOG_BUTTON_PADDING_START,
        DIALOG_BUTTON_PADDING_VERTICAL,
        buttonPadding,
        DIALOG_BUTTON_PADDING_VERTICAL
    )
}

fun Context.showDialog(
    titleRes: Int? = null,
    titleText: String? = null,
    messageText: String? = null,
    messageRes: Int? = null,
    neutralBtnRes: Int? = null,
    positiveBtnRes: Int? = null,
    negativeBtnRes: Int? = null,
    positiveAction: (() -> Unit)? = null,
    negativeAction: (() -> Unit)? = null,
    neutralAction: (() -> Unit)? = null
): AlertDialog {
    val dialog = MaterialAlertDialogBuilder(this).apply {
        messageText?.let { setMessage(it) }
        messageRes?.let { setMessage(it) }
        titleText?.let { setTitle(it) }
        titleRes?.let { setTitle(it) }
        neutralBtnRes?.let {
            setNeutralButton(neutralBtnRes) { _, _ -> neutralAction?.invoke() }
        }
        positiveBtnRes?.let {
            setPositiveButton(it) { _, _ -> positiveAction?.invoke() }
        }
        negativeBtnRes?.let {
            setNegativeButton(it) { _, _ -> negativeAction?.invoke() }
        }
    }.show()
    this.styleDialog(dialog)

    return dialog
}
