package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun addPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity): Int

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>?>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistIdPassed")
    fun getPlaylist(playlistIdPassed: Int): Flow<Playlist>

    @Query("DELETE FROM playlist_table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM playlist_table WHERE trackIds LIKE :trackIdPassed")
    suspend fun countPlaylistsContainingTrack(trackIdPassed: String): Int

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity): Int
}