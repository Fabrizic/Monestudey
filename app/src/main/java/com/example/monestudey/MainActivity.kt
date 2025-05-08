package com.example.monestudey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.monestudey.ui.ExpenseTrackerScreen
import com.example.monestudey.ui.ExpenseTrackerViewModel
import com.example.monestudey.ui.theme.MonestudeyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonestudeyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerScreen(viewModel)
                }
            }
        }
    }
}