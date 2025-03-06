package com.practicum.playlistmaker.data.dto

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track

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

    fun mapToTrackDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun mapToTrack(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }
}
