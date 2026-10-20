package com.example.triply.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 1,
    val name: String = "Traveler",
    val bio: String = "World Explorer",
    val avatarUri: String? = null,
    val currency: String = "$",
    val isDarkMode: Boolean? = null,
    val emergencyContact: String? = null,
    val medicalNotes: String? = null
)
