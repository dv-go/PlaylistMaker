package com.practicum.playlistmaker.player.domain.interactors

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun setOnTimeUpdateListener(listener: (String) -> Unit) {
        playerRepository.setOnTimeUpdateListener(listener)
    }

    override fun setOnPlaybackStateChangedListener(listener: (Boolean) -> Unit) {
        playerRepository.setOnPlaybackStateChangedListener(listener)
    }

    override fun togglePlayback() {
        playerRepository.togglePlayback()
    }

    override fun startPlayback() {
        playerRepository.startPlayback()
    }

    override fun pausePlayback() {
        playerRepository.pausePlayback()
    }

    override fun resetPlayback() {
        playerRepository.resetPlayback()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }
}