package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.ItemRepository
import com.example.ui.CollectorApp
import com.example.ui.CollectorViewModel
import com.example.ui.CollectorViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { ItemRepository(database.itemDao(), database.wishlistDao()) }

    private val viewModel: CollectorViewModel by viewModels {
        CollectorViewModelFactory(application, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CollectorApp(viewModel)
            }
        }
    }
}

