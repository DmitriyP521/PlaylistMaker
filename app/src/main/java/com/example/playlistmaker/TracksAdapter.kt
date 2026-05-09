package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private val tracks: Array<Track>
) : RecyclerView.Adapter<TracksViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TracksViewHolder {
        return TracksViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: TracksViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}