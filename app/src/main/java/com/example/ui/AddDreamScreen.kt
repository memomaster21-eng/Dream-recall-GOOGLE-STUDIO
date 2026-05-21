package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.DreamRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDreamScreen(
    repository: DreamRepository,
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: AddDreamViewModel = viewModel(factory = AddDreamViewModelFactory(repository))
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Neutral") }
    var vividness by remember { mutableStateOf(5f) }
    var isLucid by remember { mutableStateOf(false) }
    var isNightmare by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf("") }

    val moods = listOf(
        stringResource(R.string.mood_peaceful),
        stringResource(R.string.mood_happy),
        stringResource(R.string.mood_neutral),
        stringResource(R.string.mood_anxious),
        stringResource(R.string.mood_scared),
        stringResource(R.string.mood_weird)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.record_dream)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (content.isNotBlank()) {
                                viewModel.saveDream(title, content, mood, vividness, isLucid, isNightmare, tags)
                                onSave()
                            }
                        },
                        enabled = content.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.dream_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.dream_content_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Text(stringResource(R.string.mood_label), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Simplified mood selection. In a real app we might use a LazyRow or flow layout
                moods.take(4).forEach { m ->
                    FilterChip(
                        selected = mood == m,
                        onClick = { mood = m },
                        label = { Text(m) }
                    )
                }
            }

            Text(stringResource(R.string.vividness_label, vividness.toInt()), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Slider(
                value = vividness,
                onValueChange = { vividness = it },
                valueRange = 1f..10f,
                steps = 8
            )

            Text(stringResource(R.string.tags_label), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                placeholder = { Text(stringResource(R.string.tags_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isLucid,
                    onCheckedChange = { isLucid = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.lucid_dream_label), color = MaterialTheme.colorScheme.onBackground)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isNightmare,
                    onCheckedChange = { isNightmare = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE57373), checkedTrackColor = Color(0xFFE57373).copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.nightmare_label), color = MaterialTheme.colorScheme.onBackground)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
