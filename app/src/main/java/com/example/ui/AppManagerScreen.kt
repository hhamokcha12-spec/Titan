package com.example.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.GlassWhite

data class AppItemModel(
    val name: String,
    val packageName: String,
    val size: String,
    val icon: android.graphics.drawable.Drawable
)

@Composable
fun AppManagerScreen(viewModel: TitanViewModel) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppItemModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppItemModel>()
        for (appInfo in installedApps) {
            // Filter out system apps for cleaner view, but you can keep them
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                val appName = pm.getApplicationLabel(appInfo).toString()
                val icon = pm.getApplicationIcon(appInfo)
                val packageName = appInfo.packageName
                // Mocking size for demonstration, calculating actual size requires async StorageStatsManager
                val sizeVal = (10..500).random()
                appList.add(AppItemModel(appName, packageName, "$sizeVal MB", icon))
            }
        }
        apps = appList.sortedByDescending { it.size.replace(" MB", "").toInt() }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("App Manager", style = MaterialTheme.typography.headlineMedium, color = CyberBlue, fontWeight = FontWeight.Bold)
        Text("Review and uninstall heavy apps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyberBlue)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(apps) { app ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GlassWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = app.icon.toBitmap().asImageBitmap(),
                                contentDescription = app.name,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(app.size, color = CyberBlue, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { /* In Android, we'd fire an Intent to ACTION_DELETE */ }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Uninstall", tint = AlertRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
