package com.practicum.playlistmaker.search.data.dto

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryDto(private val gson: Gson) {

    fun toJson(tracks: List<TrackDto>): String {
        return gson.toJson(tracks)
    }

    fun fromJson(json: String?): List<TrackDto> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            gson.fromJson(json, type)
        }
    }
}
