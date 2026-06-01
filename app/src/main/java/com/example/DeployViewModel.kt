package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.Deployment
import com.example.db.DeploymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DeployViewModel(private val repository: DeploymentRepository) : ViewModel() {
    val deployments: StateFlow<List<Deployment>> = repository.allDeployments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isDeploying = MutableStateFlow(false)

    fun deployRepo(url: String, branch: String) {
        if (url.isBlank()) return
        viewModelScope.launch {
            isDeploying.update { true }
            
            // Simulate build analysis & deployment
            delay(2000)
            
            val commitHash = UUID.randomUUID().toString().substring(0, 7)
            val deployment = Deployment(
                repoUrl = url,
                branch = branch.ifBlank { "main" },
                commitHash = commitHash,
                status = "SUCCESS",
            )
            repository.insert(deployment)
            isDeploying.update { false }
        }
    }

    fun rollbackTo(deployment: Deployment) {
        viewModelScope.launch {
            isDeploying.update { true }
            
            // Simulate rollback delay
            delay(1500)
            
            val newDeployment = Deployment(
                repoUrl = deployment.repoUrl,
                branch = deployment.branch,
                commitHash = deployment.commitHash, // Reverting to the old commit
                status = "SUCCESS (Rollback)",
            )
            repository.insert(newDeployment)
            isDeploying.update { false }
        }
    }
}

class DeployViewModelFactory(private val repository: DeploymentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeployViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeployViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
