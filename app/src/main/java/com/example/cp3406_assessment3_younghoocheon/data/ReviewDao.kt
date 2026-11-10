package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: ReviewEntity)

    @Query("SELECT * FROM reviews ORDER BY reviewedAt ASC")
    fun observeAll(): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY reviewedAt ASC")
    suspend fun getAll(): List<ReviewEntity>

    @Query("DELETE FROM reviews")
    suspend fun deleteAll()
}