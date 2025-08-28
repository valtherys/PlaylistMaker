package com.practicum.playlistmaker.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.utils.dpToPx

class TracksAdapter(private val tracks: List<Track>) :
    ListAdapter<Track, TracksAdapter.TrackViewHolder>(TrackDiffCallback()) {

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
        private val trackTimeView = itemView.findViewById<TextView>(R.id.track_time)
        private val albumCoverView = itemView.findViewById<ImageView>(R.id.album_cover)
        private val albumCornerRadiusDp = 10f
        private val albumCornerRadiusPx = itemView.context.dpToPx(albumCornerRadiusDp)

        fun bind(model: Track) {
            val albumCoverURL = model.artworkUrl100

            trackNameView.text = model.trackName
            artistNameView.text = model.artistName
            trackTimeView.text = model.trackTime
            Glide.with(itemView)
                .load(albumCoverURL)
                .placeholder(R.drawable.ic_placeholder_45)
                .transform(RoundedCorners(albumCornerRadiusPx)).into(albumCoverView)
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track
        ): Boolean {
            return (oldItem.trackName == newItem.trackName)
                    && (oldItem.artistName == newItem.artistName)
                    && (oldItem.artworkUrl100 == newItem.artworkUrl100)
                    && (oldItem.trackTime == newItem.trackTime)
        }

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track
        ): Boolean {
            return oldItem == newItem
        }
    }
}
