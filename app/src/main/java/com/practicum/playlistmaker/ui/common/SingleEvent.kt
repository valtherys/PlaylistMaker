package com.practicum.playlistmaker.ui.common

class SingleEvent<out T>(private val content: T) {
    private var isHandled = false

    fun getContentIfNotHandled(): T?{
        return if (isHandled){
            null
        } else  {
            isHandled = true
            content
        }
    }
}