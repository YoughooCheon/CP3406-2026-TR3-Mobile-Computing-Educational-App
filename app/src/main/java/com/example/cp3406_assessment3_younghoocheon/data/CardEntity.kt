package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val front: String,
    val back: String,
    val topic: String,
    val createdAt: Long,
)