package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val recentScans: Flow<List<ScanHistory>> = appDao.getRecentScans()

    suspend fun insertScan(scan: ScanHistory) = appDao.insertScan(scan)
    
    suspend fun clearHistory() = appDao.clearHistory()
}
