package com.practicum.playlistmaker.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS playlist_table (
                playlistId INTEGER PRIMARY KEY AUTOINCREMENT,
                playlistName TEXT NOT NULL,
                playlistDescription TEXT,
                coverFilePath TEXT,
                trackIds TEXT,
                tracksAmount INTEGER NOT NULL DEFAULT 0
            )
        """)
    }
}