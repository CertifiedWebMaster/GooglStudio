package com.example

import android.app.Application
import androidx.room.Room
import com.example.db.AppDatabase
import com.example.db.DeploymentRepository

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: DeploymentRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "deployment_database"
        ).build()
        repository = DeploymentRepository(database.deploymentDao())
    }
}
