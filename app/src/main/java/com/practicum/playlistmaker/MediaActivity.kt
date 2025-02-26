package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

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
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener() {
            finish()
        }

        val isFromMain = intent.getBooleanExtra(KEY_IS_FROM_MAIN, false)

        if (isFromMain) {
            return
        }

        if (savedInstanceState != null) {
            restoreTrackData(savedInstanceState)
        } else {
            loadTrackDataFromIntent()
        }
    }

    private fun loadTrackDataFromIntent() {
        val trackName = intent.getStringExtra(KEY_TRACK_NAME) ?: ""
        val artistName = intent.getStringExtra(KEY_ARTIST_NAME) ?: ""
        val trackDuration = intent.getStringExtra(KEY_TRACK_DURATION) ?: "00:00"
        val artworkUrl = intent.getStringExtra(KEY_ARTWORK_URL)?.replace("100x100bb.jpg", "512x512bb.jpg")
        val releaseDate = intent.getStringExtra(KEY_RELEASE_DATE) ?: ""
        val genreName = intent.getStringExtra(KEY_GENRE_NAME) ?: ""
        val country = intent.getStringExtra(KEY_COUNTRY) ?: ""

        val releaseYear = if (releaseDate.length >= 4) releaseDate.substring(0, 4) else ""

        findViewById<TextView>(R.id.track_name).text = trackName
        findViewById<TextView>(R.id.track_name_value).text = trackName
        findViewById<TextView>(R.id.artist_name).text = artistName
        findViewById<TextView>(R.id.track_time_value).text = trackDuration
        findViewById<TextView>(R.id.release_date_value).text = releaseYear
        findViewById<TextView>(R.id.genre_name_value).text = genreName
        findViewById<TextView>(R.id.country_value).text = country

        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(findViewById<ImageView>(R.id.artwork_image))
    }


    private fun restoreTrackData(savedInstanceState: Bundle) {
        val trackName = savedInstanceState.getString(KEY_TRACK_NAME)
        val artistName = savedInstanceState.getString(KEY_ARTIST_NAME)
        val trackDuration = savedInstanceState.getString(KEY_TRACK_DURATION)
        val artworkUrl = savedInstanceState.getString(KEY_ARTWORK_URL)
        val releaseYear = savedInstanceState.getString(KEY_RELEASE_YEAR)
        val genreName = savedInstanceState.getString(KEY_GENRE_NAME)
        val country = savedInstanceState.getString(KEY_COUNTRY)

        if (trackName != null) findViewById<TextView>(R.id.track_name).text = trackName
        if (trackName != null) findViewById<TextView>(R.id.track_name_value).text = trackName
        if (artistName != null) findViewById<TextView>(R.id.artist_name).text = artistName
        if (trackDuration != null) findViewById<TextView>(R.id.track_time_value).text = trackDuration
        if (releaseYear != null) findViewById<TextView>(R.id.release_date_value).text = releaseYear
        if (genreName != null) findViewById<TextView>(R.id.genre_name_value).text = genreName
        if (country != null) findViewById<TextView>(R.id.country_value).text = country
        if (artworkUrl != null) {
            Glide.with(this).load(artworkUrl).into(findViewById<ImageView>(R.id.artwork_image))
        }
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
    }
}
