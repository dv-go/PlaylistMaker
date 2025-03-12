package com.practicum.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TracksAdapter(private val onTrackClick: (Track) -> Unit) : RecyclerView.Adapter<TracksViewHolder> () {

    var tracksList =  ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TracksViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = tracksList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }

    fun setTracks(newTracks: List<Track>) {
        tracksList.clear()
        tracksList.addAll(newTracks)
        notifyDataSetChanged()
    }


}