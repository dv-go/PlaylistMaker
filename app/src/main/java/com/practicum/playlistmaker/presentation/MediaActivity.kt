package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.player.MediaPlayerManager
import com.practicum.playlistmaker.domain.models.Track

class MediaActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var timerTextView: TextView
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        playButton = findViewById(R.id.play_button)
        timerTextView = findViewById(R.id.timer)

        track = savedInstanceState?.getParcelable("TRACK")
            ?: intent.getParcelableExtra("TRACK") ?: run {
                finish()
                return
            }

        loadTrackData(track)

        mediaPlayerManager = MediaPlayerManager(track.previewUrl, { time ->
            runOnUiThread { timerTextView.text = time }
        }, { isPlaying ->
            runOnUiThread { updatePlayButton(isPlaying) }
        })

        setupPlayButton()
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            mediaPlayerManager.togglePlayback()
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        playButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause_button else R.drawable.ic_play_button
        )
    }

    private fun loadTrackData(track: Track) {
        findViewById<TextView>(R.id.track_name).text = track.trackName
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

        timerTextView.text = getString(R.string.default_time_0_00)
    }

    private fun extractYear(dateString: String): String {
        return if (dateString.length >= 4) dateString.substring(0, 4) else "Unknown"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("TRACK", track)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayerManager.isInitialized) {
            mediaPlayerManager.release()
        }
    }


    override fun onPause() {
        super.onPause()
        mediaPlayerManager.pausePlayback()
    }
}
