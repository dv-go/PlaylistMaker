package com.practicum.playlistmaker.search.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkImage: ImageView = itemView.findViewById(R.id.artworkImage)

    fun bind(model: Track){
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis
        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(2))
                    .fitCenter()
            )
            .into(artworkImage)
    }
}