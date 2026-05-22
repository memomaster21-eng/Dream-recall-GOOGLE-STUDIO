package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        return HomeViewModel(repository) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: DreamRepository,
    onAddDream: () -> Unit,
    onDreamClick: (Int) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val dreams by viewModel.dreams.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("يوميات الأحلام") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlack,
                    titleContentColor = WarmWhite
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDream,
                containerColor = DimLavender,
                contentColor = DeepBlack
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة حلم")
            }
        },
        containerColor = DeepBlack
    ) { padding ->
        if (dreams.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("لم تسجل أي حلم بعد. استرخي، نم، وتذكر.", color = GrayText)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dreams) { dream ->
                    DreamCard(dream = dream, onClick = { onDreamClick(dream.id) })
                }
            }
        }
    }
}

@Composable
fun DreamCard(dream: Dream, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(dream.title.ifEmpty { "حلم بدون عنوان" }, style = MaterialTheme.typography.titleMedium, color = WarmWhite)
                if (dream.isLucid) {
                    Text("حلم جلي", style = MaterialTheme.typography.labelMedium, color = SoftCyan)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dream.content,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale("ar")).format(Date(dream.createdAt))
            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = GrayText)
        }
    }
}
