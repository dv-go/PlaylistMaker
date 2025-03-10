package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

class MediaPlayerManager(
    private val previewUrl: String,
    private val onTimeUpdate: (String) -> Unit,
    private val onPlaybackStateChanged: (Boolean) -> Unit
) {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                val currentTime = formatTime(player.currentPosition)
                onTimeUpdate(currentTime)
                handler.postDelayed(this, 500L)
            }
        }
    }

    init {
        if (previewUrl.isNotEmpty()) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                setOnPreparedListener {
                    handler.post(updateTimerRunnable)
                }
                setOnCompletionListener {
                    resetPlayback()
                }
                prepareAsync()
            }
        }
    }

    fun togglePlayback() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
        onPlaybackStateChanged(isPlaying)
    }

    fun startPlayback() {
        mediaPlayer?.let {
            try {
                if (!isPlaying) {
                    it.start()
                    isPlaying = true
                    handler.post(updateTimerRunnable)
                    onPlaybackStateChanged(true)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun pausePlayback() {
        mediaPlayer?.let {
            try {
                if (isPlaying) {
                    it.pause()
                    isPlaying = false
                    handler.removeCallbacks(updateTimerRunnable)
                    onPlaybackStateChanged(false)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun resetPlayback() {
        mediaPlayer?.let {
            try {
                if (isPlaying) {
                    it.seekTo(0)
                    isPlaying = false
                    handler.removeCallbacks(updateTimerRunnable)
                    onTimeUpdate("0:00")
                    onPlaybackStateChanged(false)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun release() {
        mediaPlayer?.let {
            try {
                handler.removeCallbacks(updateTimerRunnable)
                it.release()
                mediaPlayer = null
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        return String.format("%d:%02d", seconds / 60, seconds % 60)
    }
}
