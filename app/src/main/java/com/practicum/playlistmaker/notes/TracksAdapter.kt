package com.practicum.playlistmaker.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track

class TracksAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    ) {
        private val trackNameView = itemView.findViewById<TextView>(R.id.track_name)
        private val artistNameView = itemView.findViewById<TextView>(R.id.track_artist)
        private val albumCoverView = itemView.findViewById<ImageView>(R.id.album_cover)

        fun bind(model: Track) {
            val albumCoverURL = model.artworkUrl100

            trackNameView.text = model.trackName
            artistNameView.text = "${model.artistName} • ${model.trackTime}"
            Glide.with(itemView)
                .load(albumCoverURL)
                .placeholder(R.drawable.ic_library_music_24)
                .transform(RoundedCorners(10)).into(albumCoverView)
        }
    }
}