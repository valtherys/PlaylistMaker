package com.practicum.playlistmaker.utils

fun String.toMillis(): Long {
    val minutes = substringBefore(':').toLong()
    val seconds = substringAfter(":").toLong()
    return (minutes * 60 + seconds) * 1000
}