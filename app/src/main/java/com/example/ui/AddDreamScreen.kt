package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Dream
import com.example.data.DreamRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class AddDreamViewModel(private val repository: DreamRepository) : ViewModel() {
    fun saveDream(title: String, content: String, isLucid: Boolean, isNightmare: Boolean, isRecurring: Boolean, isFavorite: Boolean, mood: String, vividness: Float, sleepDuration: Float) {
        viewModelScope.launch {
            repository.insertDream(
                Dream(
                    title = title,
                    content = content,
                    isLucid = isLucid,
                    isNightmare = isNightmare,
                    isRecurring = isRecurring,
                    isFavorite = isFavorite,
                    mood = mood,
                    vividness = vividness,
                    sleepDuration = sleepDuration
                )
            )
        }
    }
}

class AddDreamViewModelFactory(private val repository: DreamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddDreamViewModel(repository) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDreamScreen(
    repository: DreamRepository,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val viewModel: AddDreamViewModel = viewModel(factory = AddDreamViewModelFactory(repository))
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLucid by remember { mutableStateOf(false) }
    var isNightmare by remember { mutableStateOf(false) }
    var isRecurring by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var mood by remember { mutableStateOf("عادي") }
    var vividness by remember { mutableFloatStateOf(5f) }
    var sleepDuration by remember { mutableFloatStateOf(8f) }
    
    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("حلم جديد") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    titleContentColor = MoonWhite,
                    navigationIconContentColor = MoonWhite
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الحلم (اختياري)", color = MoonWhite.copy(alpha=0.5f)) },
                    modifier = Modifier.fillMaxWidth().glassCard(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LucidBlue.copy(alpha=0.5f),
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = MoonWhite,
                        unfocusedTextColor = MoonWhite,
                        cursorColor = LucidBlue
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("ماذا حدث بالتفصيل؟", color = MoonWhite.copy(alpha=0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .glassCard(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LucidBlue.copy(alpha=0.5f),
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = MoonWhite,
                        unfocusedTextColor = MoonWhite,
                        cursorColor = LucidBlue
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )

                Text("المزاج في الحلم:", color = MoonWhite)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val moods = listOf("سعيد", "عادي", "حزين", "مخيف")
                    moods.forEach { m ->
                        FilterChip(
                            selected = mood == m,
                            onClick = { mood = m },
                            label = { Text(m) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = GlassWhite, labelColor = MoonWhite, selectedContainerColor = LucidBlue, selectedLabelColor = DeepSpace),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = mood == m, borderColor = GlassBorder)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isLucid = !isLucid }) {
                    Checkbox(
                        checked = isLucid,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = LucidBlue, checkmarkColor = DeepSpace)
                    )
                    Text("حلم جلي (كنت واعياً أثناء الحلم)", color = MoonWhite)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isNightmare = !isNightmare }) {
                    Checkbox(
                        checked = isNightmare,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = AuroraPurple, checkmarkColor = DeepSpace)
                    )
                    Text("كابوس / حلم مزعج", color = MoonWhite)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isRecurring = !isRecurring }) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = LucidBlue, checkmarkColor = DeepSpace)
                    )
                    Text("حلم متكرر", color = MoonWhite)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { isFavorite = !isFavorite }) {
                    Checkbox(
                        checked = isFavorite,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = WarningYellow, checkmarkColor = DeepSpace)
                    )
                    Text("إضافة إلى المفضلة", color = MoonWhite)
                }

                Column {
                    Text("درجة الوضوح والتذكر: ${vividness.toInt()}/10", color = MoonWhite.copy(alpha=0.7f))
                    Slider(
                        value = vividness,
                        onValueChange = { vividness = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = LucidBlue, activeTrackColor = LucidBlue.copy(alpha=0.8f), inactiveTrackColor = GlassWhite)
                    )
                }

                Column {
                    Text("ساعات النوم: ${String.format("%.1f", sleepDuration)}", color = MoonWhite.copy(alpha=0.7f))
                    Slider(
                        value = sleepDuration,
                        onValueChange = { sleepDuration = it },
                        valueRange = 0f..16f,
                        steps = 31,
                        colors = SliderDefaults.colors(thumbColor = AuroraPurple, activeTrackColor = AuroraPurple.copy(alpha=0.8f), inactiveTrackColor = GlassWhite)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (content.isNotBlank()) {
                            viewModel.saveDream(title, content, isLucid, isNightmare, isRecurring, isFavorite, mood, vividness, sleepDuration)
                            onSave()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LucidBlue, contentColor = DeepSpace),
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                ) {
                    Text("حفظ الحلم", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
