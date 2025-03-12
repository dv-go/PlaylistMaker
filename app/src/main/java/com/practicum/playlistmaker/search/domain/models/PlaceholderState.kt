package com.practicum.playlistmaker.search.domain.models

data class PlaceholderState(
    val message: String?,
    val imageRes: Int?,
    val isVisible: Boolean,
    val isRefreshButtonVisible: Boolean
)
