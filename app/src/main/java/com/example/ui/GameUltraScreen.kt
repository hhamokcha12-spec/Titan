package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlassWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameUltraScreen(viewModel: TitanViewModel) {
    var isTurboEnabled by remember { mutableStateOf(false) }
    var optimizationProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.SportsEsports, contentDescription = "Game Ultra", tint = NeonPurple, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Game Ultra Mode", style = MaterialTheme.typography.headlineLarge, color = NeonPurple, fontWeight = FontWeight.Bold)
        Text("Boost FPS & minimize latency", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        var ping by remember { mutableStateOf(45) }
        var fps by remember { mutableStateOf(60) }
        
        LaunchedEffect(isTurboEnabled) {
            if (isTurboEnabled) {
                while(true) {
                    ping = (12..18).random()
                    fps = (110..120).random()
                    delay(1500)
                }
            } else {
                while(true) {
                    ping = (40..60).random()
                    fps = (45..60).random()
                    delay(2000)
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$ping ms", style = MaterialTheme.typography.headlineMedium, color = if (isTurboEnabled) com.example.ui.theme.SuccessGreen else MaterialTheme.colorScheme.onSurface)
                Text("Network Ping", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$fps", style = MaterialTheme.typography.headlineMedium, color = if (isTurboEnabled) NeonPurple else MaterialTheme.colorScheme.onSurface)
                Text("FPS", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        if (optimizationProgress > 0f && optimizationProgress < 1f) {
            LinearProgressIndicator(
                progress = { optimizationProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = NeonPurple,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text("Allocating resources...", modifier = Modifier.padding(top = 8.dp))
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isTurboEnabled) NeonPurple.copy(alpha = 0.2f) else GlassWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (isTurboEnabled) "Turbo Active" else "Turbo Inactive", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("CPU & GPU Optimized", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = isTurboEnabled,
                        onCheckedChange = { checked -> 
                            if (checked) {
                                scope.launch {
                                    optimizationProgress = 0.01f
                                    for (i in 1..100) {
                                        delay(10)
                                        optimizationProgress = i / 100f
                                    }
                                    isTurboEnabled = true
                                }
                            } else {
                                isTurboEnabled = false
                                optimizationProgress = 0f
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple, checkedTrackColor = NeonPurple.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}
