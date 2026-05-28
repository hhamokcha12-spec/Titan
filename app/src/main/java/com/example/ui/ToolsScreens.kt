package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                progress = { state.scanProgress },
                modifier = Modifier.size(100.dp),
                color = CyberBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 8.dp
            )
            Text("${(state.scanProgress * 100).toInt()}%", modifier = Modifier.padding(top = 16.dp))
        } else if (showResult) {
            Text("Optimization Complete!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary)
            Text("Freed up $resultText", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { showResult = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) { Text("Done") }
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
                    Text("DEEP CLEAN", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background, style = MaterialTheme.typography.titleLarge)
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
                    .fillMaxWidth()
                    .height(16.dp),
                color = NeonPurple,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else if (showResult) {
            Text("RAM Boosted!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary)
            Text("Cleared $resultText", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { showResult = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) { Text("Done") }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Button(
                    onClick = {
                        viewModel.startRamBoost { saved ->
                            resultText = saved
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
                    Text("RAM BOOST", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
