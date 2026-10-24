package com.example.datecycle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.datecycle.navigation.DateCycleNavGraph
import com.example.datecycle.ui.DateCycleViewModel
import com.example.datecycle.ui.theme.DateCycleTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DateCycleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            DateCycleTheme(
                themeMode = uiState.themeMode,
                dynamicColor = uiState.dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DateCycleNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
