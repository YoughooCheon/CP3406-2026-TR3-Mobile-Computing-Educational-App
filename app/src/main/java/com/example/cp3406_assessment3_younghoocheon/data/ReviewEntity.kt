package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Int,
    val topic: String,
    val rating: Int,
    val reviewedAt: Long,
    val dayEpoch: Long,
)