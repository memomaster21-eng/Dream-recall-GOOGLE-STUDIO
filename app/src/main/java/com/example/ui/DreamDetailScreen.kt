package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Dream
import com.example.data.DreamRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.ai.AiService

class DreamDetailViewModel(private val repository: DreamRepository, private val dreamId: Int) : ViewModel() {
    val dream = repository.getDreamById(dreamId)
    
    var isAnalyzing by mutableStateOf(false)
        private set

    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            repository.deleteDream(dream)
        }
    }

    fun analyzeDream(dream: Dream, apiKey: String) {
        if (apiKey.isBlank()) return
        viewModelScope.launch {
            isAnalyzing = true
            val aiService = AiService(apiKey)
            val analysis = aiService.analyzeDream(dream.title, dream.content, dream.isLucid, dream.isRecurring)
            repository.updateDream(dream.copy(aiAnalysis = analysis))
            isAnalyzing = false
        }
    }
}

class DreamDetailViewModelFactory(private val repository: DreamRepository, private val dreamId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DreamDetailViewModel(repository, dreamId) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamDetailScreen(
    dreamId: Int,
    repository: DreamRepository,
    onBack: () -> Unit
) {
    val viewModel: DreamDetailViewModel = viewModel(factory = DreamDetailViewModelFactory(repository, dreamId))
    val dream by viewModel.dream.collectAsState(initial = null)

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("تفاصيل الحلم") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    dream?.let {
                        IconButton(onClick = {
                            viewModel.deleteDream(it)
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MoonWhite,
                    navigationIconContentColor = MoonWhite
                )
            )

            dream?.let { d ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = d.title.ifEmpty { "حلم بدون عنوان" },
                        style = MaterialTheme.typography.headlineLarge,
                        color = MoonWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val dateStr = SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm", Locale("ar")).format(Date(d.createdAt))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonWhite.copy(alpha=0.5f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (d.isLucid) {
                            SuggestionChip(
                                onClick = {}, 
                                label = { Text("حلم جلي", color = LucidBlue) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = GlassWhite),
                                border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = LucidBlue.copy(alpha=0.5f))
                            )
                        }
                        if (d.isNightmare) {
                            SuggestionChip(
                                onClick = {}, 
                                label = { Text("كابوس", color = AuroraPurple) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = GlassWhite),
                                border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = AuroraPurple.copy(alpha=0.5f))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = d.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MoonWhite.copy(alpha=0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    val context = LocalContext.current
                    val sharedPrefs = remember { context.getSharedPreferences("dream_settings", Context.MODE_PRIVATE) }
                    val apiKey = sharedPrefs.getString("gemini_api_key", "") ?: ""
                    
                    if (d.aiAnalysis != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassCard(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                                .padding(24.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LucidBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تحليل الذكاء الاصطناعي", style = MaterialTheme.typography.titleMedium, color = LucidBlue)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(d.aiAnalysis, style = MaterialTheme.typography.bodyMedium, color = MoonWhite)
                            }
                        }
                    } else if (apiKey.isNotBlank()) {
                        Button(
                            onClick = { viewModel.analyzeDream(d, apiKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = AuroraPurple.copy(alpha=0.3f), contentColor = MoonWhite),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = !viewModel.isAnalyzing
                        ) {
                            if (viewModel.isAnalyzing) {
                                CircularProgressIndicator(color = LucidBlue, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AuroraPurple)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("تحليل الحلم بواسطة Gemini", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    } else {
                        Text("أضف مفتاح Gemini API في الإعدادات لتفعيل تحليل الأحلام.", style = MaterialTheme.typography.labelSmall, color = MoonWhite.copy(alpha=0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = LucidBlue)
                }
            }
        }
    }
}
