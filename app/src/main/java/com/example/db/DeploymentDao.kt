package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeploymentDao {
    @Query("SELECT * FROM deployments ORDER BY timestamp DESC")
    fun getAllDeployments(): Flow<List<Deployment>>

    @Insert
    suspend fun insertDeployment(deployment: Deployment)
}
