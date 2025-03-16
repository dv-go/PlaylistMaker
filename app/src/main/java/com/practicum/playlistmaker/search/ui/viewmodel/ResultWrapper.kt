package com.practicum.playlistmaker.search.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed class ResultWrapper {
    data class Success(val tracks: List<Track>) : ResultWrapper()
    object Empty : ResultWrapper()
    object NetworkError : ResultWrapper()
}
