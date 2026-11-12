package com.example.cp3406_assessment3_younghoocheon.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ReviewEntity::class, CardEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao
    abstract fun cardDao(): CardDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `cards` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`front` TEXT NOT NULL, " +
                            "`back` TEXT NOT NULL, " +
                            "`topic` TEXT NOT NULL, " +
                            "`createdAt` INTEGER NOT NULL)",
                )
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "memorydesk.db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}