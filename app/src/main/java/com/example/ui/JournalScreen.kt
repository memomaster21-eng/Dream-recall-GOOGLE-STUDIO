package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Dream
import com.example.data.DreamRepository
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    repository: DreamRepository,
    onAddDream: () -> Unit,
    onDreamClick: (Int) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val dreams by viewModel.dreams.collectAsState(initial = emptyList())
    
    var searchQuery by remember { mutableStateOf("") }
    var filterFavorites by remember { mutableStateOf(false) }
    var filterLucid by remember { mutableStateOf(false) }
    var filterMood by remember { mutableStateOf("الكل") }

    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 120.dp) // Bottom padding for dock
        ) {
            Text(
                text = "يوميات الأحلام",
                style = MaterialTheme.typography.headlineMedium,
                color = MoonWhite,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .glassCard(RoundedCornerShape(24.dp)),
                placeholder = { Text("ابحث في عقلك الباطن...", color = MoonWhite.copy(alpha=0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = MoonWhite.copy(alpha=0.7f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LucidBlue.copy(alpha=0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MoonWhite,
                    unfocusedTextColor = MoonWhite,
                    cursorColor = LucidBlue
                ),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = filterFavorites,
                    onClick = { filterFavorites = !filterFavorites },
                    label = { Text("المفضلة") },
                    leadingIcon = { Icon(if (filterFavorites) Icons.Default.Star else Icons.Outlined.Star, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = GlassWhite, labelColor = MoonWhite, iconColor = MoonWhite, selectedContainerColor = LucidBlue, selectedLabelColor = DeepSpace, selectedLeadingIconColor = DeepSpace),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = filterFavorites, borderColor = GlassBorder)
                )
                FilterChip(
                    selected = filterLucid,
                    onClick = { filterLucid = !filterLucid },
                    label = { Text("الأحلام الجلية") },
                    leadingIcon = { Icon(Icons.Outlined.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(containerColor = GlassWhite, labelColor = MoonWhite, iconColor = MoonWhite, selectedContainerColor = LucidBlue, selectedLabelColor = DeepSpace, selectedLeadingIconColor = DeepSpace),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = filterLucid, borderColor = GlassBorder)
                )
                
                var expanded by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = filterMood != "الكل",
                        onClick = { expanded = true },
                        label = { Text(if (filterMood == "الكل") "المزاج" else filterMood) },
                        leadingIcon = { Icon(Icons.Outlined.Mood, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(containerColor = GlassWhite, labelColor = MoonWhite, iconColor = MoonWhite, selectedContainerColor = AuroraPurple.copy(alpha=0.6f), selectedLabelColor = MoonWhite, selectedLeadingIconColor = MoonWhite),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = filterMood != "الكل", borderColor = GlassBorder)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DeepSpace).border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                    ) {
                        listOf("الكل", "سعيد", "عادي", "حزين", "مخيف").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = MoonWhite) },
                                onClick = {
                                    filterMood = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            val filteredDreams = dreams.filter {
                val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
                val matchesFavorite = if (filterFavorites) it.isFavorite else true
                val matchesLucid = if (filterLucid) it.isLucid else true
                val matchesMood = if (filterMood == "الكل") true else it.mood == filterMood
                
                matchesSearch && matchesFavorite && matchesLucid && matchesMood
            }

            if (filteredDreams.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("لم تسجل أي حلم بعد. استرخي، نم، وتذكر.", color = MoonWhite.copy(alpha=0.5f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDreams) { dream ->
                        JournalDreamCard(dream, onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDreamClick(dream.id) 
                        })
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAddDream()
            },
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
fun JournalDreamCard(dream: Dream, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale("ar")).format(Date(dream.createdAt))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.5f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (dream.isFavorite) {
                        Icon(Icons.Default.Star, contentDescription = "Favorite", tint = WarningYellow, modifier = Modifier.size(16.dp))
                    }
                    if (dream.isLucid) {
                        Icon(Icons.Default.WaterDrop, contentDescription = "Lucid", tint = LucidBlue, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(dream.title.ifEmpty { "حلم بدون عنوان" }, style = MaterialTheme.typography.titleLarge, color = MoonWhite)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (dream.isLucid) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("جلي", color = LucidBlue, style = MaterialTheme.typography.labelSmall) },
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = LucidBlue.copy(alpha=0.5f)),
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = GlassWhite)
                    )
                }
                if (dream.mood != "عادي" && dream.mood.isNotBlank()) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(dream.mood, color = MoonWhite, style = MaterialTheme.typography.labelSmall) },
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = GlassBorder),
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = GlassWhite)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = dream.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MoonWhite.copy(alpha=0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
