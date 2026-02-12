package com.practicum.playlistmaker.ui.medialibrary.playlists.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.utils.dpToPx

class PlaylistsAdapter() : ListAdapter<Playlist, PlaylistsAdapter.PlaylistsViewHolder>(
    PlaylistDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        val playlist = getItem(position)
        holder.bind(playlist)
    }


    class PlaylistsViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val playlistCornerRadiusPx = itemView.context.dpToPx(PLAYLIST_CORNER_RADIUS)

        fun bind(model: Playlist) {
            val playlistCoverUri = model.coverFilePath

            binding.apply {
                etPlaylistName.text = model.playlistName
                etPlaylistDescription.text = "${model.tracksAmount} треков"

                Glide.with(itemView).load(playlistCoverUri)
                    .placeholder(R.drawable.ic_placeholder_45)
                    .error(R.drawable.ic_placeholder_45)
                    .transform(CenterCrop(), RoundedCorners(playlistCornerRadiusPx))
                    .into(binding.ivPlaylistCover)
            }
        }
    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.playlistId == newItem.playlistId
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val PLAYLIST_CORNER_RADIUS = 8f
    }

}

