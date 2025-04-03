package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.presentation.PlayerScreenState
import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var timerTextView: TextView
    private lateinit var track: Track

    private val mediaViewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        playButton = findViewById(R.id.play_button)
        timerTextView = findViewById(R.id.timer)

        track = savedInstanceState?.getSerializable("TRACK") as? Track
            ?: intent.getSerializableExtra("TRACK") as? Track
                    ?: throw IllegalArgumentException("Track is missing in Intent")

        setupObservers()
        setupPlayButton()
    }

    private fun setupObservers() {
        mediaViewModel.screenState.observe(this) { state ->
            when (state) {
                is PlayerScreenState.Loading -> showLoading()
                is PlayerScreenState.Error -> showError()
                is PlayerScreenState.Content -> showContent(state)
            }
        }
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            mediaViewModel.togglePlayback()
        }
    }

    private fun showLoading() {}

    private fun showError() {}

    private fun showContent(state: PlayerScreenState.Content) {
        with(state) {
            findViewById<TextView>(R.id.track_name).text = track.trackName
            findViewById<TextView>(R.id.artist_name).text = track.artistName
            findViewById<TextView>(R.id.track_time_value).text = track.trackTimeMillis
            findViewById<TextView>(R.id.release_date_value).text = releaseYear
            findViewById<TextView>(R.id.genre_name_value).text = genre
            findViewById<TextView>(R.id.country_value).text = country

            timerTextView.text = currentTime
            playButton.setImageResource(playButtonIcon)

            Glide.with(this@PlayerActivity)
                .load(artworkUrl)
                .placeholder(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .into(findViewById<ImageView>(R.id.artwork_image))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("TRACK", track)
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
