package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.db.Deployment
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
  private val viewModel: DeployViewModel by viewModels {
    DeployViewModelFactory((application as MyApplication).repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
          DeployerScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
          )
        }
      }
    }
  }
}

@Composable
fun DeployerScreen(modifier: Modifier = Modifier, viewModel: DeployViewModel) {
    val deployments by viewModel.deployments.collectAsStateWithLifecycle()
    val isDeploying by viewModel.isDeploying.collectAsStateWithLifecycle()
    
    var repoUrl by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEVELOPER CONSOLE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "DeployFlow",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = repoUrl,
                    onValueChange = { repoUrl = it },
                    label = { Text("GitHub Repo URL") },
                    placeholder = { Text("https://github.com/user/repo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isDeploying,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Branch (optional)") },
                    placeholder = { Text("main") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isDeploying,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.deployRepo(repoUrl, branch) },
                    enabled = !isDeploying && repoUrl.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (isDeploying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("New Deployment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Active Deployments",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        
        if (deployments.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No deployments yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(deployments) { index, deployment ->
                    val isCurrentLive = deployments.indexOfFirst { 
                        it.repoUrl == deployment.repoUrl && it.status.startsWith("SUCCESS") 
                    } == index
                    
                    DeploymentCard(
                        deployment = deployment,
                        isCurrentLive = isCurrentLive,
                        isDeploying = isDeploying,
                        onRollback = { viewModel.rollbackTo(deployment) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeploymentCard(
    deployment: Deployment,
    isCurrentLive: Boolean,
    isDeploying: Boolean,
    onRollback: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val dateString = formatter.format(Date(deployment.timestamp))
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deployment.repoUrl.substringAfterLast("/").ifEmpty { deployment.repoUrl },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Branch: ${deployment.branch} • Commit: ${deployment.commitHash}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                if (isCurrentLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deployed $dateString",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (!isCurrentLive && deployment.status.startsWith("SUCCESS")) {
                    Text(
                        text = "ROLLBACK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(end = 4.dp).clickable(enabled = !isDeploying) { onRollback() }
                    )
                }
            }
        }
    }
}
