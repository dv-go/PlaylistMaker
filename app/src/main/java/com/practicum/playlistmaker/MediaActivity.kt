package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val isFromMain = intent.getBooleanExtra("IS_FROM_MAIN", false)

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
        val trackName = intent.getStringExtra("TRACK_NAME") ?: ""
        val artistName = intent.getStringExtra("ARTIST_NAME") ?: ""
        val trackDuration = intent.getStringExtra("TRACK_DURATION") ?: "00:00"
        val artworkUrl = intent.getStringExtra("ARTWORK_URL")?.replace("100x100bb.jpg", "512x512bb.jpg")
        val releaseDate = intent.getStringExtra("RELEASE_DATE") ?: ""
        val genreName = intent.getStringExtra("GENRE_NAME") ?: ""
        val country = intent.getStringExtra("COUNTRY") ?: ""

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
        val trackName = savedInstanceState.getString("TRACK_NAME")
        val artistName = savedInstanceState.getString("ARTIST_NAME")
        val trackDuration = savedInstanceState.getString("TRACK_DURATION")
        val artworkUrl = savedInstanceState.getString("ARTWORK_URL")
        val releaseYear = savedInstanceState.getString("RELEASE_YEAR")
        val genreName = savedInstanceState.getString("GENRE_NAME")
        val country = savedInstanceState.getString("COUNTRY")

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
        outState.putString("TRACK_NAME", findViewById<TextView>(R.id.track_name).text.toString())
        outState.putString("ARTIST_NAME", findViewById<TextView>(R.id.artist_name).text.toString())
        outState.putString("TRACK_DURATION", findViewById<TextView>(R.id.track_time_value).text.toString())
        outState.putString("ARTWORK_URL", intent.getStringExtra("ARTWORK_URL"))
        outState.putString("RELEASE_YEAR", findViewById<TextView>(R.id.release_date_value).text.toString())
        outState.putString("GENRE_NAME", findViewById<TextView>(R.id.genre_name_value).text.toString())
        outState.putString("COUNTRY", findViewById<TextView>(R.id.country_value).text.toString())
    }
}
