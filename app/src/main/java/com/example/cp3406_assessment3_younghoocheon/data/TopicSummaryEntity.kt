package com.example.cp3406_assessment3_younghoocheon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A Wikipedia summary saved on the device, so a topic guide can open without a connection. */
@Entity(tableName = "topic_summaries")
data class TopicSummaryEntity(
    @PrimaryKey val title: String,
    val description: String,
    val extract: String,
    val pageUrl: String,
    val fetchedAt: Long,
)
