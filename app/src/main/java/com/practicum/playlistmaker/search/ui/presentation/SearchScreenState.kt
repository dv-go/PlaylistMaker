package com.practicum.playlistmaker.search.ui.presentation

import com.practicum.playlistmaker.search.domain.models.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Loaded(val tracks: List<Track>) : SearchScreenState()
    data class Error(val errorType: ErrorType) : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()
    object EmptyHistory : SearchScreenState()

    object Typing : SearchScreenState()

    data class ShowToast(val message: String) : SearchScreenState()

    sealed class ErrorType {
        object NoNetwork : ErrorType()
        object NoResults : ErrorType()
    }
}