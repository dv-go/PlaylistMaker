package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun setOnTimeUpdateListener(listener: (String) -> Unit)
    fun setOnPlaybackStateChangedListener(listener: (Boolean) -> Unit)
    fun togglePlayback()
    fun startPlayback()
    fun pausePlayback()
    fun resetPlayback()
    fun release()
    fun isPlaying(): Boolean
}
