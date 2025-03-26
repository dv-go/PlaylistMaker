package com.practicum.playlistmaker.player.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val previewUrl: String,
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    private var onTimeUpdate: ((String) -> Unit)? = null
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            val currentTime = formatTime(mediaPlayer.currentPosition)
            onTimeUpdate?.invoke(currentTime)
            handler.postDelayed(this, 500L)
        }
    }

    init {
        if (previewUrl.isNotEmpty()) {
            mediaPlayer.apply {
                try {
                    setDataSource(previewUrl)
                    setOnPreparedListener {
                        handler.post(updateTimerRunnable)
                    }
                    setOnCompletionListener {
                        resetPlayback()
                    }
                    prepareAsync()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun setOnTimeUpdateListener(listener: (String) -> Unit) {
        onTimeUpdate = listener
    }

    override fun setOnPlaybackStateChangedListener(listener: (Boolean) -> Unit) {
        onPlaybackStateChanged = listener
    }

    override fun togglePlayback() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
        onPlaybackStateChanged?.invoke(isPlaying)
    }

    override fun startPlayback() {
        if (!isPlaying) {
            mediaPlayer.start()
            isPlaying = true
            handler.post(updateTimerRunnable)
            onPlaybackStateChanged?.invoke(true)
        }
    }

    override fun pausePlayback() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
            handler.removeCallbacks(updateTimerRunnable)
            onPlaybackStateChanged?.invoke(false)
        }
    }

    override fun resetPlayback() {
        mediaPlayer.seekTo(0)
        isPlaying = false
        handler.removeCallbacks(updateTimerRunnable)
        onTimeUpdate?.invoke("0:00")
        onPlaybackStateChanged?.invoke(false)
    }

    override fun release() {
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        return String.format("%d:%02d", seconds / 60, seconds % 60)
    }
}
