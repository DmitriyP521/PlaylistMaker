package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter() : RecyclerView.Adapter<TracksViewHolder> () {

    private var tracks = listOf<Track>()
    private var onItemClickCallback: ((Track) -> Unit)? = null

    fun setOnItemClickListener(callback: (Track) -> Unit) {
        onItemClickCallback = callback
    }

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
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickCallback?.invoke(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun submitList(newTracks: List<Track>) {
        tracks = newTracks.toMutableList()
        notifyDataSetChanged()
    }
}