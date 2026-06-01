package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deployments")
data class Deployment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val repoUrl: String,
    val branch: String,
    val commitHash: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
