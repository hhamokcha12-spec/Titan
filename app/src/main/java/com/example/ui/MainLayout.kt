package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.NeonPurple
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute
@Serializable
object CleanRoute
@Serializable
object BoostRoute
@Serializable
object ChatRoute
@Serializable
object AppManagerRoute
@Serializable
object BatteryGuardRoute
@Serializable
object GameUltraRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TitanViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                val screens = listOf(
                    Triple("Dashboard", Icons.Default.Dashboard, DashboardRoute::class.qualifiedName),
                    Triple("Clean", Icons.Default.CleaningServices, CleanRoute::class.qualifiedName),
                    Triple("Boost", Icons.Default.Memory, BoostRoute::class.qualifiedName),
                    Triple("AI Chat", Icons.Default.SmartToy, ChatRoute::class.qualifiedName)
                )

                screens.forEach { (title, icon, route) ->
                    val selected = currentDestination == route
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = selected,
                        onClick = {
                            if (route != null && !selected) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberBlue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            selectedTextColor = CyberBlue,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_$title".lowercase())
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            ParticleBackground()
            NavHost(
                navController = navController,
                startDestination = DashboardRoute::class.qualifiedName!!
            ) {
                composable(DashboardRoute::class.qualifiedName!!) { DashboardScreen(viewModel, navController) }
                composable(CleanRoute::class.qualifiedName!!) { CleanScreen(viewModel) }
                composable(BoostRoute::class.qualifiedName!!) { BoostScreen(viewModel) }
                composable(ChatRoute::class.qualifiedName!!) { ChatScreen(viewModel) }
                composable(AppManagerRoute::class.qualifiedName!!) { AppManagerScreen(viewModel) }
                composable(BatteryGuardRoute::class.qualifiedName!!) { BatteryGuardScreen(viewModel) }
                composable(GameUltraRoute::class.qualifiedName!!) { GameUltraScreen(viewModel) }
            }
        }
    }
}
