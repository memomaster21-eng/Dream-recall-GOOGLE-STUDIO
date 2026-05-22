package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Dream
import com.example.data.DreamRepository
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(private val repository: DreamRepository) : ViewModel() {
    val dreams = repository.allDreams
}

class HomeViewModelFactory(private val repository: DreamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: DreamRepository,
    onJournalClick: () -> Unit,
    onAddDreamClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val dreams by viewModel.dreams.collectAsState(initial = emptyList())

    val currentStreak = remember(dreams) {
        if (dreams.isEmpty()) return@remember "0 أيام"
        
        val offset = java.util.TimeZone.getDefault().rawOffset
        val oneDayMs = 24 * 60 * 60 * 1000L
        val days = dreams.map { (it.createdAt + offset) / oneDayMs }.distinct().sortedDescending()
        
        val currentDay = (System.currentTimeMillis() + offset) / oneDayMs
        
        if (!days.contains(currentDay) && !days.contains(currentDay - 1)) {
            return@remember "0 أيام"
        }
        
        var streak = 0
        var expectedDay = currentDay
        
        for (day in days) {
            if (day == expectedDay || day == expectedDay - 1) {
                if (day != expectedDay) {
                    expectedDay = day
                }
                streak++
                expectedDay--
            } else {
                break
            }
        }
        "$streak أيام"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .auroraBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column {
                Text("صباح الخير،", style = MaterialTheme.typography.headlineLarge, color = MoonWhite)
                Spacer(modifier = Modifier.height(8.dp))
                Text("لقد قام عقلك الباطن برحلة طويلة الليلة الماضية.", style = MaterialTheme.typography.bodyLarge, color = MoonWhite.copy(alpha = 0.7f))
            }
            
            Button(
                onClick = onSleepClick,
                colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple.copy(alpha=0.3f), contentColor = MoonWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Cloud, contentDescription = null, tint = AuroraPurple)
                Spacer(modifier = Modifier.width(16.dp))
                Text("بدء وضع النوم", style = MaterialTheme.typography.titleMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "إجمالي الأحلام",
                    value = dreams.size.toString(),
                    icon = { Icon(Icons.Default.Cloud, contentDescription = null, tint = LucidBlue, modifier = Modifier.breathingPulse()) }
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "السلسلة الحالية",
                    value = currentStreak,
                    icon = { Icon(Icons.Default.FlashOn, contentDescription = null, tint = AuroraPurple, modifier = Modifier.breathingPulse()) }
                )
            }

            // Recent Voyage
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("الرحلة الأخيرة", style = MaterialTheme.typography.titleLarge, color = MoonWhite)
                TextButton(onClick = onJournalClick) {
                    Text("السجل", color = LucidBlue)
                }
            }
            
            if (dreams.isNotEmpty()) {
                val latestDream = dreams.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard(RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(latestDream.title.ifEmpty { "حلم بدون عنوان" }, style = MaterialTheme.typography.titleMedium, color = MoonWhite)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"${latestDream.content}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MoonWhite.copy(alpha = 0.8f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        val timeStr = SimpleDateFormat("hh:mm a", Locale("ar")).format(Date(latestDream.createdAt))
                        Text(timeStr, style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.4f))
                    }
                }
            } else {
                Text("لا توجد أحلام مسجلة بعد. استرخي، نم، وتذكر.", color = MoonWhite.copy(alpha=0.5f))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddDreamClick,
            containerColor = LucidBlue,
            contentColor = DeepSpace,
            shape = RoundedCornerShape(50.dp), // Orb
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 120.dp)
                .breathingPulse(minScale = 0.95f, maxScale = 1.05f)
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة حلم")
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .height(160.dp)
            .glassCard(RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            icon()
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.5f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, style = MaterialTheme.typography.headlineLarge, color = MoonWhite)
            }
        }
    }
}