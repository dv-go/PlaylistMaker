package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit

class RetrofitNetworkClient(
    private val trackService: ITunesSearchAPI
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            val response = trackService.search(dto.term).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
