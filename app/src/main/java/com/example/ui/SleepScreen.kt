package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DreamRepository
import com.example.data.SleepSession
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SleepViewModel(private val repository: DreamRepository) : ViewModel() {
    var activeSession by mutableStateOf<SleepSession?>(null)
        private set

    init {
        viewModelScope.launch {
            activeSession = repository.getActiveSleepSession()
        }
    }

    fun startSleep() {
        viewModelScope.launch {
            val session = SleepSession(startTime = System.currentTimeMillis())
            repository.insertSleepSession(session)
            activeSession = repository.getActiveSleepSession()
        }
    }

    fun recordFazza() {
        viewModelScope.launch {
            activeSession?.let { session ->
                // Basic JSON array append for string
                val time = System.currentTimeMillis()
                val newFazzas = if (session.fazzaTimes == "[]") "[$time]" else session.fazzaTimes.replace("]", ",$time]")
                val updatedSession = session.copy(fazzaTimes = newFazzas)
                repository.updateSleepSession(updatedSession)
                activeSession = updatedSession
            }
        }
    }

    fun wakeUp(onFinished: (Float) -> Unit) {
        viewModelScope.launch {
            activeSession?.let { session ->
                val endTime = System.currentTimeMillis()
                val updatedSession = session.copy(endTime = endTime, isCompleted = true)
                repository.updateSleepSession(updatedSession)
                activeSession = null
                
                val durationMs = endTime - session.startTime
                val durationHours = durationMs / (1000f * 60 * 60)
                onFinished(durationHours)
            }
        }
    }
}

class SleepViewModelFactory(private val repository: DreamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SleepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun SleepScreen(repository: DreamRepository, onWakeUp: (Float) -> Unit) {
    val viewModel: SleepViewModel = viewModel(factory = SleepViewModelFactory(repository))
    val activeSession = viewModel.activeSession

    var currentTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (activeSession != null) Color.Black else DeepSpace)
    ) {
        if (activeSession == null) {
            // Not sleeping
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Cloud,
                    contentDescription = null,
                    tint = AuroraPurple,
                    modifier = Modifier.size(80.dp).breathingPulse()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("حان وقت الراحة", style = MaterialTheme.typography.headlineMedium, color = MoonWhite)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "ابدأ وضع النوم الآن. سيقوم التطبيق بتتبع نومك بصمت. إذا استيقظت ليلاً، يمكنك تسجيل الفزّة فوراً.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MoonWhite.copy(alpha=0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = { viewModel.startSleep() },
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple, contentColor = MoonWhite),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("بدء النوم", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            // Sleeping
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = currentTime,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoonWhite.copy(alpha = 0.2f)
                    )
                )
                
                Column(
                    modifier = Modifier.padding(bottom = 64.dp, start = 32.dp, end = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Button(
                        onClick = { viewModel.recordFazza() },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassWhite, contentColor = LucidBlue),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    ) {
                        Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("تسجيل استيقاظ (فزّة)", style = MaterialTheme.typography.titleMedium)
                    }

                    Button(
                        onClick = { viewModel.wakeUp(onWakeUp) },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningYellow.copy(alpha=0.8f), contentColor = DeepSpace),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("إنهاء النوم والاستيقاظ", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
