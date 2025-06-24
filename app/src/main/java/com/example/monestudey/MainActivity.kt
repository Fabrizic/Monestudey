package com.example.monestudey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.monestudey.ui.AccountSetupScreen
import com.example.monestudey.ui.ExpenseTrackerScreen
import com.example.monestudey.ui.ExpenseTrackerViewModel
import com.example.monestudey.ui.WelcomeScreen
import com.example.monestudey.ui.chatbot.ChatbotScreen
import com.example.monestudey.ui.chatbot.ChatbotViewModel
import com.example.monestudey.ui.theme.MonestudeyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseTrackerViewModel by viewModels()
    private val chatbotViewModel: ChatbotViewModel by viewModels()
    private val PREFS_NAME = "MonestudeyPrefs"
    private val FIRST_TIME_KEY = "isFirstTime"
    private val ACCOUNT_SETUP_KEY = "isAccountSetup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true)
        val isAccountSetup = sharedPreferences.getBoolean(ACCOUNT_SETUP_KEY, false)

        setContent {
            MonestudeyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showWelcome by remember { mutableStateOf(isFirstTime) }
                    var showAccountSetup by remember { mutableStateOf(!isAccountSetup && !isFirstTime) }
                    var selectedScreen by remember { mutableStateOf(0) }

                    when {
                        showWelcome -> {
                            WelcomeScreen(
                                onStartClick = {
                                    sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply()
                                    showWelcome = false
                                    showAccountSetup = true
                                }
                            )
                        }
                        showAccountSetup -> {
                            AccountSetupScreen(
                                onSetupComplete = {
                                    sharedPreferences.edit().putBoolean(ACCOUNT_SETUP_KEY, true).apply()
                                    showAccountSetup = false
                                }
                            )
                        }
                        else -> {
                            Scaffold(
                                bottomBar = {
                                    NavigationBar {
                                        NavigationBarItem(
                                            icon = { Icon(Icons.Default.Home, contentDescription = "Gastos") },
                                            label = { Text("Gastos") },
                                            selected = selectedScreen == 0,
                                            onClick = { selectedScreen = 0 }
                                        )
                                        NavigationBarItem(
                                            icon = { Icon(Icons.Default.Info, contentDescription = "Chatbot") },
                                            label = { Text("Chatbot") },
                                            selected = selectedScreen == 1,
                                            onClick = { selectedScreen = 1 }
                                        )
                                    }
                                }
                            ) { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    when (selectedScreen) {
                                        0 -> ExpenseTrackerScreen(viewModel)
                                        1 -> ChatbotScreen(chatbotViewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}