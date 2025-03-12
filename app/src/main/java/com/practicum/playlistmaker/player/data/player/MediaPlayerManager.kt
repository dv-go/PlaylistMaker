package com.practicum.playlistmaker.player.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

class MediaPlayerManager(
    private val previewUrl: String
) {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    private var onTimeUpdate: ((String) -> Unit)? = null
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                val currentTime = formatTime(player.currentPosition)
                onTimeUpdate?.invoke(currentTime)
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

    fun setOnTimeUpdateListener(listener: (String) -> Unit) {
        onTimeUpdate = listener
    }

    fun setOnPlaybackStateChangedListener(listener: (Boolean) -> Unit) {
        onPlaybackStateChanged = listener
    }

    fun togglePlayback() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
        onPlaybackStateChanged?.invoke(isPlaying)
    }

    fun startPlayback() {
        mediaPlayer?.let {
            if (!isPlaying) {
                it.start()
                isPlaying = true
                handler.post(updateTimerRunnable)
                onPlaybackStateChanged?.invoke(true)
            }
        }
    }

    fun pausePlayback() {
        mediaPlayer?.let {
            if (isPlaying) {
                it.pause()
                isPlaying = false
                handler.removeCallbacks(updateTimerRunnable)
                onPlaybackStateChanged?.invoke(false)
            }
        }
    }

    fun resetPlayback() {
        mediaPlayer?.let {
            it.seekTo(0)
            isPlaying = false
            handler.removeCallbacks(updateTimerRunnable)
            onTimeUpdate?.invoke("0:00")
            onPlaybackStateChanged?.invoke(false)
        }
    }

    fun release() {
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        return String.format("%d:%02d", seconds / 60, seconds % 60)
    }
}
