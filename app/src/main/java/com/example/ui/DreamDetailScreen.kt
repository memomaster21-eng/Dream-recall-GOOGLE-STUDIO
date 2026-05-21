package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.DreamRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamDetailScreen(
    dreamId: Int,
    repository: DreamRepository,
    onBack: () -> Unit,
    viewModel: DreamDetailViewModel = viewModel(factory = DreamDetailViewModelFactory(repository, dreamId))
) {
    val dream by viewModel.dream.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dream_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dream_dialog_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDream()
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.delete), color = Color(0xFFE57373))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = Color(0xFFE57373))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        dream?.let { d ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy • h:mm a", Locale.getDefault())
                Text(
                    text = dateFormat.format(Date(d.createdAt)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = d.title.ifEmpty { stringResource(R.string.untitled_dream) },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    if (d.isLucid) {
                        TagChip(text = stringResource(R.string.lucid_tag), color = MaterialTheme.colorScheme.primary)
                    }
                    if (d.isNightmare) {
                        TagChip(text = stringResource(R.string.nightmare_tag), color = Color(0xFFE57373))
                    }
                    TagChip(text = d.mood, color = MaterialTheme.colorScheme.secondary)
                    TagChip(text = stringResource(R.string.vividness_label, d.vividness), color = MaterialTheme.colorScheme.tertiary)
                }

                if (d.tags.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.symbols_label, d.tags),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                Text(
                    text = d.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
