package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}