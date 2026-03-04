package com.practicum.playlistmaker.ui.playlist.adapter

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.common.adapters.TracksAdapter

class TracksBottomSheetAdapter(
    private val onLongClick: (Track) -> Unit,
    private val onItemClick: (Track) -> Unit
) : TracksAdapter(onItemClick) {
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val track = getItem(position)
        holder.bind(track)
        holder.binding.root.setOnClickListener {
            onItemClick(track)
        }

        holder.binding.root.setOnLongClickListener {
            onLongClick(track)
            true
        }
    }
}