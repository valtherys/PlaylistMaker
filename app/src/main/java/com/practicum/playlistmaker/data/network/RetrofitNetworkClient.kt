package com.practicum.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.data.search.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(val service: ITunesApiService, val context: Context) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()){
            return Response().apply { resultCode = HttpStatusCodes.CONNECTION_ERROR }
        }
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = HttpStatusCodes.BAD_REQUEST }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = service.searchTracks(dto.expression)
                response.apply { resultCode = HttpStatusCodes.SUCCESS_MIN }
            } catch (e: Throwable) {
                Response().apply { resultCode = HttpStatusCodes.SERVER_ERROR }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}