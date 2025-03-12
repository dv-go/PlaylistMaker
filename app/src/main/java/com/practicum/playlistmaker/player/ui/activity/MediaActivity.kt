package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.player.MediaPlayerManager
import com.practicum.playlistmaker.player.ui.viewmodel.MediaViewModel
import com.practicum.playlistmaker.player.ui.viewmodel.MediaViewModelFactory
import com.practicum.playlistmaker.search.domain.models.Track

class MediaActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var timerTextView: TextView
    private lateinit var track: Track

    private val mediaViewModel: MediaViewModel by lazy {
        val previewUrl = track.previewUrl ?: ""
        val mediaPlayerManager = MediaPlayerManager(previewUrl)
        ViewModelProvider(this, MediaViewModelFactory(mediaPlayerManager, track))[MediaViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        playButton = findViewById(R.id.play_button)
        timerTextView = findViewById(R.id.timer)

        track = savedInstanceState?.getParcelable("TRACK")
            ?: intent.getParcelableExtra("TRACK") ?: run {
                finish()
                return
            }

        setupObservers()
        setupPlayButton()
    }

    private fun setupObservers() {
        mediaViewModel.currentTime.observe(this) { time ->
            timerTextView.text = time
        }

        mediaViewModel.isPlaying.observe(this) { isPlaying ->
            updatePlayButton(isPlaying)
        }

        mediaViewModel.trackData.observe(this) { trackData ->
            findViewById<TextView>(R.id.track_name).text = trackData["trackName"]
            findViewById<TextView>(R.id.artist_name).text = trackData["artistName"]
            findViewById<TextView>(R.id.track_time_value).text = trackData["trackTime"]
            findViewById<TextView>(R.id.release_date_value).text = trackData["releaseYear"]
            findViewById<TextView>(R.id.genre_name_value).text = trackData["genre"]
            findViewById<TextView>(R.id.country_value).text = trackData["country"]
        }

        mediaViewModel.artworkUrl.observe(this) { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .into(findViewById<ImageView>(R.id.artwork_image))
        }

        mediaViewModel.defaultTime.observe(this) { time ->
            timerTextView.text = time
        }

        mediaViewModel.playButtonIcon.observe(this) { iconRes ->
            playButton.setImageResource(iconRes)
        }
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            mediaViewModel.togglePlayback()
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        playButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause_button else R.drawable.ic_play_button
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("TRACK", track)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaViewModel.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        mediaViewModel.pausePlayback()
    }
}
