package com.example.db

import kotlinx.coroutines.flow.Flow

class DeploymentRepository(private val deploymentDao: DeploymentDao) {
    val allDeployments: Flow<List<Deployment>> = deploymentDao.getAllDeployments()

    suspend fun insert(deployment: Deployment) = deploymentDao.insertDeployment(deployment)
}
