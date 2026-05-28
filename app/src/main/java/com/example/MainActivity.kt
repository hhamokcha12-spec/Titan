package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.MainScreen
import com.example.ui.TitanViewModel
import com.example.ui.TitanViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "titan_database"
        ).build()
        val repository = AppRepository(database.appDao())
        val factory = TitanViewModelFactory(repository, application)
        val viewModel = ViewModelProvider(this, factory)[TitanViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}

