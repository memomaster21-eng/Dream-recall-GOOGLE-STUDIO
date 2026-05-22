package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

class DreamDetailViewModel(private val repository: DreamRepository, private val dreamId: Int) : ViewModel() {
    val dream = repository.getDreamById(dreamId)

    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            repository.deleteDream(dream)
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

    Scaffold(
        topBar = {
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
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlack,
                    titleContentColor = WarmWhite,
                    navigationIconContentColor = WarmWhite
                )
            )
        },
        containerColor = DeepBlack
    ) { padding ->
        dream?.let { d ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = d.title.ifEmpty { "حلم بدون عنوان" },
                    style = MaterialTheme.typography.titleLarge,
                    color = WarmWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                val dateStr = SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm", Locale("ar")).format(Date(d.createdAt))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (d.isLucid) {
                        SuggestionChip(onClick = {}, label = { Text("حلم جلي", color = SoftCyan) })
                    }
                    if (d.isNightmare) {
                        SuggestionChip(onClick = {}, label = { Text("كابوس", color = DimLavender) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = d.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WarmWhite
                )
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = SoftCyan)
            }
        }
    }
}
