package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insert(card: CardEntity)

    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    suspend fun getAll(): List<CardEntity>

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cards")
    suspend fun deleteAll()
}