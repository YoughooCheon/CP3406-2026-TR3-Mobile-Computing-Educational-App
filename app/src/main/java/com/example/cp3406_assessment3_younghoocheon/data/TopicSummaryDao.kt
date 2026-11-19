package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TopicSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: TopicSummaryEntity)

    @Query("SELECT * FROM topic_summaries WHERE title = :title LIMIT 1")
    suspend fun get(title: String): TopicSummaryEntity?

    @Query("DELETE FROM topic_summaries")
    suspend fun deleteAll()
}
