package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: PlaylistTrackEntity): Long

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity): Int

    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackIdPassed")
    suspend fun deleteTrackById(trackIdPassed: String): Int

    @Query("SELECT * FROM playlist_track_table WHERE trackId IN (:ids)")
    fun getPlaylistTracks(ids: List<String>): Flow<List<PlaylistTrackEntity>>
}