package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.data.network.HttpStatusCodes

open class Response() {
    var resultCode = HttpStatusCodes.CONNECTION_ERROR
}