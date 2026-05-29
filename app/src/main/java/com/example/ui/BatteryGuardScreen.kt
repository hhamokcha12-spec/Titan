package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.SuccessGreen

@Composable
fun BatteryGuardScreen(viewModel: TitanViewModel) {
    val context = LocalContext.current
    var batteryPct by remember { mutableStateOf(100f) }
    var batteryTemp by remember { mutableStateOf(0f) }
    var isCharging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        batteryPct = if (level != -1 && scale != -1) (level * 100f / scale) else 100f
        
        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        batteryTemp = temp / 10f // Returns in tenths of a degree Celsius
        
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }

    val animatedPct by animateFloatAsState(
        targetValue = batteryPct / 100f,
        animationSpec = tween(1500),
        label = "battery_anim"
    )

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Battery Guard", style = MaterialTheme.typography.headlineMedium, color = SuccessGreen, fontWeight = FontWeight.Bold)
        Text("AI Battery Life Extender", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            val waveAnim = rememberInfiniteTransition(label = "wave")
            val phase by waveAnim.animateFloat(
                initialValue = 0f, 
                targetValue = 2f * Math.PI.toFloat(), 
                animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
                label = "phase"
            )
            
            val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
            
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val waveColor = if (batteryPct > 20) SuccessGreen else AlertRed
                drawCircle(
                    color = surfaceVariantColor,
                    radius = size.width / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16.dp.toPx())
                )
                
                // Draw wave for battery inside
                val path = androidx.compose.ui.graphics.Path()
                val waterLevel = size.height * (1f - animatedPct)
                val amplitude = 15f
                path.moveTo(0f, size.height)
                path.lineTo(0f, waterLevel)
                
                for (x in 0..size.width.toInt() step 5) {
                    val y = waterLevel + kotlin.math.sin(phase + (x / size.width) * 4f * Math.PI.toFloat()) * amplitude
                    path.lineTo(x.toFloat(), y.toFloat())
                }
                
                path.lineTo(size.width, size.height)
                path.close()
                
                clipPath(androidx.compose.ui.graphics.Path().apply { addOval(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height)) }) {
                    drawPath(
                        path = path,
                        color = waveColor.copy(alpha = 0.5f)
                    )
                }
                
                drawArc(
                    color = waveColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedPct,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BatteryChargingFull,
                    contentDescription = null,
                    tint = if (isCharging) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(48.dp)
                )
                Text("${batteryPct.toInt()}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BatteryStat("Temperature", "${batteryTemp}°C")
            BatteryStat("Status", if (isCharging) "Charging" else "Discharging")
            BatteryStat("Health", "Good")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = GlassWhite), shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AlertRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("3 Apps draining battery", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Tap to optimize background usage", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun BatteryStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
