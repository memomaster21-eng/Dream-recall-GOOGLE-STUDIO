package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.DreamDatabase
import com.example.data.DreamRepository
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = DreamDatabase.getDatabase(this)
        val repository = DreamRepository(database.dreamDao())
        val sharedPrefs = getSharedPreferences("dream_prefs", android.content.Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppNavigation(
                        dreamRepository = repository,
                        isFirstLaunch = isFirstLaunch,
                        onCompleteOnboarding = {
                            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                        }
                    )
                }
            }
        }
    }
}
