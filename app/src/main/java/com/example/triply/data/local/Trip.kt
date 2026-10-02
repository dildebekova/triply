package com.example.triply.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val location: String,
    val startDate: Long,
    val endDate: Long,
    val budget: Double,
    val description: String
)
