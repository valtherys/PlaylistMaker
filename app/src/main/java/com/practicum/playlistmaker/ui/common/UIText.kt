package com.practicum.playlistmaker.ui.common

sealed class UIText {
    data class Plural(val resId: Int, val amount: Int): UIText()
}