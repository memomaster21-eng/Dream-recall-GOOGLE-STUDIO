package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DreamRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(repository: DreamRepository) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val dreams by viewModel.dreams.collectAsState(initial = emptyList())
    
    // Calculates
    val totalDreams = dreams.size
    val lucidCount = dreams.count { it.isLucid }
    val nightmareCount = dreams.count { it.isNightmare }
    val averageVividness = if (dreams.isNotEmpty()) dreams.map { it.vividness }.average() else 0.0
    
    val lucidPercent = if (totalDreams > 0) ((lucidCount.toFloat() / totalDreams) * 100).toInt() else 0
    val normalPercent = if (totalDreams > 0) (((totalDreams - lucidCount - nightmareCount).coerceAtLeast(0).toFloat() / totalDreams) * 100).toInt() else 0
    val nightmarePercent = if (totalDreams > 0) ((nightmareCount.toFloat() / totalDreams) * 100).toInt() else 0

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = "الإحصائيات",
                style = MaterialTheme.typography.headlineMedium,
                color = MoonWhite
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                BasicStatCard(
                    modifier = Modifier.weight(1f),
                    title = "معدل الأحلام الجلية",
                    value = "$lucidPercent%",
                    subtitle = "$lucidCount أحلام"
                )
                BasicStatCard(
                    modifier = Modifier.weight(1f),
                    title = "متوسط الوضوح",
                    value = String.format("%.1f", averageVividness),
                    subtitle = "/ 10"
                )
            }

            // Real Chart container 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("أنواع الأحلام", style = MaterialTheme.typography.titleLarge, color = MoonWhite)
                        Text("إجمالي $totalDreams", style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.5f))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(LucidBlue.copy(alpha=0.15f), androidx.compose.foundation.shape.CircleShape)
                                .border(1.dp, LucidBlue.copy(alpha=0.3f), androidx.compose.foundation.shape.CircleShape)
                                .breathingPulse(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(dreams.size.toString(), style = MaterialTheme.typography.headlineMedium, color = LucidBlue)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendItem(color = LucidBlue, label = "جلي", percent = "$lucidPercent%")
                            LegendItem(color = GlassWhite, label = "عادي", percent = "$normalPercent%")
                            LegendItem(color = AuroraPurple, label = "كابوس", percent = "$nightmarePercent%")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, percent: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, androidx.compose.foundation.shape.CircleShape))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MoonWhite, modifier = Modifier.width(60.dp))
        Text(percent, style = MaterialTheme.typography.bodyMedium, color = MoonWhite.copy(alpha=0.5f))
    }
}

@Composable
fun BasicStatCard(modifier: Modifier = Modifier, title: String, value: String, subtitle: String) {
    Box(
        modifier = modifier
            .height(140.dp)
            .glassCard(RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.5f))
            Column {
                Text(value, style = MaterialTheme.typography.headlineLarge, color = MoonWhite)
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = LucidBlue)
                }
            }
        }
    }
}