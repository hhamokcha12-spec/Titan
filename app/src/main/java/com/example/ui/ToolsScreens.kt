package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay

@Composable
fun CleanScreen(viewModel: TitanViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showResult by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isScanning) {
            Text("Deep Scanning Data...", color = CyberBlue, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                progress = { state.scanProgress },
                modifier = Modifier.size(120.dp),
                color = CyberBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 10.dp
            )
            Text("${(state.scanProgress * 100).toInt()}%", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(32.dp))
            Card(colors = CardDefaults.cardColors(containerColor = GlassWhite), shape = RoundedCornerShape(12.dp)) {
                Text("Analyzing cache, temp files, and obsolete APKs...", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        } else if (showResult) {
            Text("Optimization Complete!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
            Text("Freed up $resultText", style = MaterialTheme.typography.headlineMedium, color = CyberBlue, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { showResult = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) { Text("Done", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Button(
                    onClick = {
                        viewModel.startDeepClean { saved ->
                            resultText = saved
                            showResult = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .testTag("btn_clean"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DEEP CLEAN", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background, style = MaterialTheme.typography.titleLarge)
                        Text("Analyze & Free Space", color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun BoostScreen(viewModel: TitanViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showResult by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    var killedAppsList by remember { mutableStateOf<List<String>>(emptyList()) }
    
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isScanning) {
            Text("Terminating background processes...", color = NeonPurple, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { state.scanProgress },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp),
                color = NeonPurple,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text("${(state.scanProgress * 100).toInt()}%", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.titleLarge)
        } else if (showResult) {
            Text("RAM Boosted!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
            Text("Cleared $resultText", style = MaterialTheme.typography.headlineMedium, color = NeonPurple, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            
            if (killedAppsList.isNotEmpty()) {
                Text("Optimized ${killedAppsList.size} background apps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false).padding(horizontal = 8.dp)
                ) {
                    items(killedAppsList.size) { i ->
                        Card(colors = CardDefaults.cardColors(containerColor = GlassWhite), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(killedAppsList[i], style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                Text("Stopped", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Button(
                onClick = { showResult = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) { Text("Done", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Button(
                    onClick = {
                        viewModel.startRamBoost { saved, apps ->
                            resultText = saved
                            killedAppsList = apps
                            showResult = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .testTag("btn_boost"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RAM BOOST", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
                        Text("Speed up device", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
