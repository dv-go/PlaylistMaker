package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    companion object {
        const val KEY_IS_FROM_MAIN = "IS_FROM_MAIN"
        const val KEY_TRACK_NAME = "TRACK_NAME"
        const val KEY_ARTIST_NAME = "ARTIST_NAME"
        const val KEY_TRACK_DURATION = "TRACK_DURATION"
        const val KEY_ARTWORK_URL = "ARTWORK_URL"
        const val KEY_RELEASE_DATE = "RELEASE_DATE"
        const val KEY_RELEASE_YEAR = "RELEASE_YEAR"
        const val KEY_GENRE_NAME = "GENRE_NAME"
        const val KEY_COUNTRY = "COUNTRY"
        const val KEY_PREVIEW_URL = "PREVIEW_URL"
    }

    private lateinit var playButton: ImageView
    private lateinit var timerTextView: TextView

    private var previewUrl: String = ""

    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingFlag = false

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener() {
            finish()
        }

        val isFromMain = intent.getBooleanExtra(KEY_IS_FROM_MAIN, false)

        playButton = findViewById(R.id.play_button)
        timerTextView = findViewById(R.id.timer)

        val track = when {
            savedInstanceState != null -> restoreTrackData(savedInstanceState)
            isFromMain -> getDefaultTrack()
            else -> getIntentTrack()
        }

        loadTrackDataFromIntent(track)
        previewUrl = track.previewUrl

        setupPlayButton()
    }

    private fun getDefaultTrack(): Track {
        return Track(
            trackId = 0,
            trackName = getString(R.string.album_name),
            artistName = getString(R.string.group_name),
            trackTimeMillis = "5:35",
            artworkUrl100 = "",
            collectionName = null,
            releaseDate = "1965",
            primaryGenreName = "Rock",
            country = "Великобритания",
            previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/1c/67/9a/1c679a61-a1cd-e7ae-9b58-630e595e7b2a/mzaf_12558727020823763101.plus.aac.p.m4a"
        )
    }

    private fun getIntentTrack(): Track {
        return Track(
            trackId = intent.getIntExtra("TRACK_ID", 0),
            trackName = intent.getStringExtra(KEY_TRACK_NAME) ?: "Unknown Track",
            artistName = intent.getStringExtra(KEY_ARTIST_NAME) ?: "Unknown Artist",
            trackTimeMillis = intent.getStringExtra(KEY_TRACK_DURATION) ?: "0:00",
            artworkUrl100 = intent.getStringExtra(KEY_ARTWORK_URL) ?: "",
            collectionName = null,
            releaseDate = intent.getStringExtra(KEY_RELEASE_DATE) ?: "Unknown Year",
            primaryGenreName = intent.getStringExtra(KEY_GENRE_NAME) ?: "Unknown Genre",
            country = intent.getStringExtra(KEY_COUNTRY) ?: "Unknown Country",
            previewUrl = intent.getStringExtra(KEY_PREVIEW_URL) ?: ""
        )
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            if (isPlayingFlag) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun startPlayback() {
        if (previewUrl.isNullOrEmpty()) {
            return
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                setOnPreparedListener {
                    start()
                    isPlayingFlag = true
                    playButton.setImageResource(R.drawable.ic_pause_button)
                    handler.post(updateTimerRunnable)
                }
                setOnCompletionListener {
                    resetPlayback()
                }
                prepareAsync()
            }
        } else {
            mediaPlayer?.start()
            isPlayingFlag = true
            playButton.setImageResource(R.drawable.ic_pause_button)
            handler.post(updateTimerRunnable)
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlayingFlag = false
        playButton.setImageResource(R.drawable.ic_play_button)
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun resetPlayback() {
        isPlayingFlag = false
        playButton.setImageResource(R.drawable.ic_play_button)
        mediaPlayer?.seekTo(0)
        handler.removeCallbacks(updateTimerRunnable)
        updateTimer()
    }

    private fun updateTimer() {
        mediaPlayer?.let { player ->
            if (!isDestroyed) {
                val currentTime = String.format(Locale.getDefault(), "%d:%02d",
                    (player.currentPosition / 1000) / 60,
                    (player.currentPosition / 1000) % 60
                )
                timerTextView.text = currentTime
            }
        }
    }

    private fun loadTrackDataFromIntent(track: Track) {
        findViewById<TextView>(R.id.track_name).text = track.trackName
        findViewById<TextView>(R.id.track_name_value).text = track.trackName
        findViewById<TextView>(R.id.artist_name).text = track.artistName
        findViewById<TextView>(R.id.track_time_value).text = track.trackTimeMillis
        findViewById<TextView>(R.id.release_date_value).text = extractYear(track.releaseDate)
        findViewById<TextView>(R.id.genre_name_value).text = track.primaryGenreName
        findViewById<TextView>(R.id.country_value).text = track.country

        if (track.artworkUrl100.startsWith("http")) {
            Glide.with(this)
                .load(track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .into(findViewById<ImageView>(R.id.artwork_image))
        } else {
            findViewById<ImageView>(R.id.artwork_image).setImageResource(R.drawable.album)
        }

        timerTextView.text = "0:00"
    }

    private fun extractYear(dateString: String): String {
        return if (dateString.length >= 4) dateString.substring(0, 4) else "Unknown"
    }

    private fun restoreTrackData(savedInstanceState: Bundle): Track {
        return Track(
            trackId = 0,
            trackName = savedInstanceState.getString(KEY_TRACK_NAME) ?: "",
            artistName = savedInstanceState.getString(KEY_ARTIST_NAME) ?: "",
            trackTimeMillis = savedInstanceState.getString(KEY_TRACK_DURATION) ?: "00:00",
            artworkUrl100 = savedInstanceState.getString(KEY_ARTWORK_URL) ?: "",
            collectionName = null,
            releaseDate = savedInstanceState.getString(KEY_RELEASE_YEAR) ?: "",
            primaryGenreName = savedInstanceState.getString(KEY_GENRE_NAME) ?: "",
            country = savedInstanceState.getString(KEY_COUNTRY) ?: "",
            previewUrl = savedInstanceState.getString(KEY_PREVIEW_URL) ?: ""
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TRACK_NAME, findViewById<TextView>(R.id.track_name).text.toString())
        outState.putString(KEY_ARTIST_NAME, findViewById<TextView>(R.id.artist_name).text.toString())
        outState.putString(KEY_TRACK_DURATION, findViewById<TextView>(R.id.track_time_value).text.toString())
        outState.putString(KEY_ARTWORK_URL, intent.getStringExtra(KEY_ARTWORK_URL))
        outState.putString(KEY_RELEASE_YEAR, findViewById<TextView>(R.id.release_date_value).text.toString())
        outState.putString(KEY_GENRE_NAME, findViewById<TextView>(R.id.genre_name_value).text.toString())
        outState.putString(KEY_COUNTRY, findViewById<TextView>(R.id.country_value).text.toString())
        outState.putString(KEY_PREVIEW_URL, previewUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
