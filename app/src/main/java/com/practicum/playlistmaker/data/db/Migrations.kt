package com.practicum.playlistmaker.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_table (
                playlistId INTEGER PRIMARY KEY AUTOINCREMENT,
                playlistName TEXT NOT NULL,
                playlistDescription TEXT,
                coverFilePath TEXT,
                trackIds TEXT,
                tracksAmount INTEGER NOT NULL DEFAULT 0
            )
        """
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS playlist_track_table (
                        trackId TEXT NOT NULL,
                        trackName TEXT,
                        artistName TEXT,
                        trackTime TEXT,
                        artworkUrl100 TEXT,
                        artworkUrlPlayer TEXT,
                        collectionName TEXT,
                        releaseDate TEXT,
                        primaryGenreName TEXT,
                        country TEXT,
                        previewUrl TEXT,
                        PRIMARY KEY(trackId)
                    )
                    """
        )
    }
}