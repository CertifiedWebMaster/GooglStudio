package com.example.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Deployment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deploymentDao(): DeploymentDao
}
