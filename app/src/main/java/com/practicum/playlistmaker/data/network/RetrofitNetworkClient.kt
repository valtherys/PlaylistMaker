package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.search.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(val service: ITunesApiService) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val resp = service.searchTracks(dto.expression).execute()
            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = HttpStatusCodes.BAD_REQUEST }
        }
    }
}