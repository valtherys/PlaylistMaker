package com.practicum.playlistmaker.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.utils.dpToPx

class TracksAdapter(private val onItemClick: (Track) -> Unit) :
    ListAdapter<Track, TracksAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
        holder.binding.root.setOnClickListener {
            onItemClick(track)
        }
    }

    class TrackViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        private val albumCornerRadiusDp = ALBUM_CORNER_RADIUS_DP.toFloat()
        private val albumCornerRadiusPx = itemView.context.dpToPx(albumCornerRadiusDp)

        fun bind(model: Track) {
            val albumCoverURL = model.artworkUrl100

            binding.trackName.text = model.trackName
            binding.trackArtist.text = model.artistName
            binding.trackTime.text = model.trackTime
            Glide.with(itemView)
                .load(albumCoverURL)
                .placeholder(R.drawable.ic_placeholder_45)
                .transform(RoundedCorners(albumCornerRadiusPx)).into(binding.albumCover)
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track
        ): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object{
        private const val ALBUM_CORNER_RADIUS_DP = "10f"
    }
}
