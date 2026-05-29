package com.example.ui

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppRepository
import com.example.data.ScanHistory
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class TitanState(
    val usedRamPercent: Int = 0,
    val totalRamGb: Float = 0f,
    val usedStoragePercent: Int = 0,
    val totalStorageGb: Float = 0f,
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val aiChatHistory: List<ChatMessage> = emptyList(),
    val isAiTyping: Boolean = false,
    val killedApps: List<String> = emptyList()
)

data class ChatMessage(val text: String, val isUser: Boolean)

class TitanViewModel(private val repository: AppRepository, private val context: Context) : ViewModel() {

    private val _state = MutableStateFlow(TitanState())
    val state = _state.asStateFlow()

    init {
        refreshStats()
        // Welcome message
        _state.value = _state.value.copy(
            aiChatHistory = listOf(ChatMessage("Welcome to Titan Cleaner AI! 🚀 I'm your device optimization assistant. How can I speed up your phone today?", false))
        )
    }

    fun refreshStats() {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        
        val totalRam = memInfo.totalMem.toFloat() / (1024 * 1024 * 1024)
        val availRam = memInfo.availMem.toFloat() / (1024 * 1024 * 1024)
        val usedRam = totalRam - availRam
        val ramPercent = ((usedRam / totalRam) * 100).toInt()

        val stat = StatFs(Environment.getDataDirectory().path)
        val totalStorage = stat.totalBytes.toFloat() / (1024 * 1024 * 1024)
        val availStorage = stat.availableBytes.toFloat() / (1024 * 1024 * 1024)
        val usedStorage = totalStorage - availStorage
        val storagePercent = ((usedStorage / totalStorage) * 100).toInt()

        _state.value = _state.value.copy(
            usedRamPercent = ramPercent.takeIf { it in 0..100 } ?: 0,
            totalRamGb = totalRam,
            usedStoragePercent = storagePercent.takeIf { it in 0..100 } ?: 0,
            totalStorageGb = totalStorage
        )
    }

    fun startDeepClean(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanProgress = 0f)
            for (i in 1..100) {
                delay(20)
                _state.value = _state.value.copy(scanProgress = i / 100f)
            }
            // Simulate saving space
            val savedSpace = "1." + (2..9).random() + " GB"
            repository.insertScan(ScanHistory(type = "CLEANUP", dataSaved = savedSpace))
            _state.value = _state.value.copy(isScanning = false)
            refreshStats()
            onComplete(savedSpace)
        }
    }
    
    fun startRamBoost(onComplete: (String, List<String>) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanProgress = 0f, killedApps = emptyList())
            
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pm = context.packageManager
            val packages = pm.getInstalledPackages(0)
            val killedAppNames = mutableListOf<String>()

            for (i in 1..100) {
                delay(15)
                _state.value = _state.value.copy(scanProgress = i / 100f)
                
                if (i % 15 == 0) {
                    val randomApp = packages.randomOrNull()
                    if (randomApp != null && randomApp.packageName != context.packageName) {
                        val appName = try {
                            pm.getApplicationLabel(pm.getApplicationInfo(randomApp.packageName, 0)).toString()
                        } catch (e: Exception) { randomApp.packageName }
                        
                        if (appName != randomApp.packageName && appName.length < 20) {
                            killedAppNames.add(appName)
                        }
                        try { am.killBackgroundProcesses(randomApp.packageName) } catch (e: Exception) {}
                    }
                }
            }

            val savedRam = (300..900).random().toString() + " MB"
            repository.insertScan(ScanHistory(type = "BOOST", dataSaved = savedRam))
            _state.value = _state.value.copy(isScanning = false, killedApps = killedAppNames)
            refreshStats()
            onComplete(savedRam, killedAppNames)
        }
    }

    fun sendAiMessage(message: String) {
        val currentHistory = _state.value.aiChatHistory.toMutableList()
        currentHistory.add(ChatMessage(message, true))
        _state.value = _state.value.copy(aiChatHistory = currentHistory, isAiTyping = true)

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                val promptContents = currentHistory.map {
                    Content(
                        parts = listOf(Part(text = it.text)),
                        role = if (it.isUser) "user" else "model"
                    )
                }

                val currentRamUsed = _state.value.usedRamPercent
                val currentStorageUsed = _state.value.usedStoragePercent

                val request = GenerateContentRequest(
                    contents = promptContents,
                    systemInstruction = Content(parts = listOf(Part(text = "You are Titan Cleaner AI, a professional Android optimization assistant. Keep responses very short, helpful, and focused on device performance (cleaning, battery, RAM, apps). The user's device currently has ${currentRamUsed}% RAM usage and ${currentStorageUsed}% storage used. Give hyper-specific optimization advice acting as a high-tech AI system.")))
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val replyText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I am experiencing network difficulties analyzing your device."
                
                val newHistory = _state.value.aiChatHistory.toMutableList()
                newHistory.add(ChatMessage(replyText, false))
                _state.value = _state.value.copy(aiChatHistory = newHistory, isAiTyping = false)
            } catch (e: Exception) {
                val newHistory = _state.value.aiChatHistory.toMutableList()
                newHistory.add(ChatMessage("Error connecting to Titan AI cores: ${e.message}", false))
                _state.value = _state.value.copy(aiChatHistory = newHistory, isAiTyping = false)
            }
        }
    }
}
