package com.example.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.Dream
import com.example.data.DreamRepository
import com.example.ui.theme.*
import com.example.worker.ReminderHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(repository: DreamRepository, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPrefs = remember { context.getSharedPreferences("dream_settings", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }
    
    var isReminderEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("reminder_enabled", false)) }
    var periodDays by remember { mutableStateOf(sharedPrefs.getInt("reminder_period_days", 7)) }
    var frequencyHours by remember { mutableStateOf(sharedPrefs.getLong("reminder_frequency_hours", 2L)) }
    var rawApiKey by remember { mutableStateOf(sharedPrefs.getString("gemini_api_key", "") ?: "") }
    var isEditingApiKey by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isReminderEnabled = true
            sharedPrefs.edit().putBoolean("reminder_enabled", true).apply()
            ReminderHelper.scheduleReminders(context, frequencyHours)
        } else {
            isReminderEnabled = false
            sharedPrefs.edit().putBoolean("reminder_enabled", false).apply()
            ReminderHelper.cancelReminders(context)
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    val dreams = repository.allDreams.firstOrNull() ?: emptyList()
                    val jsonString = gson.toJson(dreams)
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    }
                    Toast.makeText(context, "تم تصدير الأحلام بنجاح", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "فشل التصدير: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val jsonString = reader.readText()
                        val type = object : TypeToken<List<Dream>>() {}.type
                        val dreams: List<Dream> = gson.fromJson(jsonString, type)
                        dreams.forEach { dream ->
                            // Import defaults for missing properties
                            val newDream = dream.copy(id = 0) // reset ID to auto-generate
                            repository.insertDream(newDream)
                        }
                        Toast.makeText(context, "تم استيراد ${dreams.size} أحلام بنجاح", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "فشل الاستيراد: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            
            Text(
                text = "الإعدادات",
                style = MaterialTheme.typography.headlineMedium,
                color = MoonWhite
            )

            // Appereance (Mock)
            SettingsSection(title = "المظهر") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("وضع المظهر", style = MaterialTheme.typography.titleMedium, color = MoonWhite)
                    Text("داكن", color = MoonWhite.copy(alpha=0.5f))
                }
            }

            // AI Settings
            SettingsSection(title = "الذكاء الاصطناعي (Gemini)") {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (isEditingApiKey || rawApiKey.isBlank()) {
                        OutlinedTextField(
                            value = rawApiKey,
                            onValueChange = { rawApiKey = it },
                            label = { Text("Gemini API Key", color = MoonWhite.copy(alpha=0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LucidBlue,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = MoonWhite,
                                unfocusedTextColor = MoonWhite
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                sharedPrefs.edit().putString("gemini_api_key", rawApiKey.trim()).apply()
                                isEditingApiKey = false
                                Toast.makeText(context, "تم حفظ المفتاح بنجاح", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LucidBlue, contentColor = DeepSpace)
                        ) {
                            Text("حفظ المفتاح")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("المفتاح محفوظ ومفعل", style = MaterialTheme.typography.titleMedium, color = SuccessGreen)
                                Text("الذكاء الاصطناعي يقوم بتحليل الأحلام والمساعدة.", style = MaterialTheme.typography.bodySmall, color = MoonWhite.copy(alpha=0.5f))
                            }
                            TextButton(onClick = { isEditingApiKey = true }) {
                                Text("تعديل", color = LucidBlue)
                            }
                        }
                    }
                }
            }

            // Reminders
            SettingsSection(title = "محفزات الأحلام") {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("محفزات الوعي (AI)", style = MaterialTheme.typography.titleMedium, color = MoonWhite)
                            Text(if (rawApiKey.isNotBlank()) "إشعارات محفزة بالذكاء الاصطناعي" else "تتطلب إضافة مفتاح Gemini", style = MaterialTheme.typography.bodySmall, color = if (rawApiKey.isNotBlank()) MoonWhite.copy(alpha=0.5f) else WarningYellow)
                        }
                        Switch(
                            checked = isReminderEnabled,
                            enabled = rawApiKey.isNotBlank(),
                            onCheckedChange = { checked ->
                                if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    isReminderEnabled = checked
                                    sharedPrefs.edit().putBoolean("reminder_enabled", checked).apply()
                                    if (checked) {
                                        ReminderHelper.scheduleReminders(context, frequencyHours)
                                    } else {
                                        ReminderHelper.cancelReminders(context)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = LucidBlue, checkedTrackColor = AuroraPurple.copy(alpha=0.5f), uncheckedTrackColor = GlassWhite, uncheckedBorderColor = GlassBorder, uncheckedThumbColor = MoonWhite.copy(alpha=0.5f))
                        )
                    }

                    if (isReminderEnabled) {
                        Spacer(modifier = Modifier.height(24.dp))
                        OptionSelection(
                            title = "فترة الأحلام للاختيار منها",
                            options = listOf(7 to "آخر أسبوع", 30 to "آخر شهر"),
                            selectedOption = periodDays,
                            onOptionSelected = {
                                periodDays = it
                                sharedPrefs.edit().putInt("reminder_period_days", it).apply()
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OptionSelection(
                            title = "معدل التنبيه",
                            options = listOf(1L to "1س", 2L to "2س", 4L to "4س", 12L to "12س"),
                            selectedOption = frequencyHours,
                            onOptionSelected = {
                                frequencyHours = it
                                sharedPrefs.edit().putLong("reminder_frequency_hours", it).apply()
                                ReminderHelper.scheduleReminders(context, it)
                            }
                        )
                    }
                }
            }

            // Memory Management
            SettingsSection(title = "إدارة البيانات") {
                Column {
                    SettingsItem(
                        icon = Icons.Default.CloudUpload,
                        title = "تصدير كـ JSON",
                        onClick = { exportLauncher.launch("dreams_export.json") }
                    )
                    SettingsItem(
                        icon = Icons.Default.CloudDownload,
                        title = "استيراد من JSON",
                        onClick = { importLauncher.launch(arrayOf("application/json")) }
                    )
                    SettingsItem(
                        icon = Icons.Default.DeleteForever,
                        title = "مسح بيانات العقل الباطن",
                        onClick = { },
                        iconTint = DangerRed,
                        textColor = DangerRed
                    )
                }
            }
            
            Text("إصدار التطبيق 1.0.0", color = MoonWhite.copy(alpha=0.4f), style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = LucidBlue, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().glassCard(RoundedCornerShape(32.dp))
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconTint: Color = MoonWhite.copy(alpha=0.7f),
    textColor: Color = MoonWhite
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}

@Composable
fun <T> OptionSelection(title: String, options: List<Pair<T, String>>, selectedOption: T, onOptionSelected: (T) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MoonWhite.copy(alpha=0.6f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (value, label) ->
                FilterChip(
                    selected = value == selectedOption,
                    onClick = { onOptionSelected(value) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AuroraPurple,
                        selectedLabelColor = MoonWhite,
                        containerColor = GlassWhite,
                        labelColor = MoonWhite.copy(alpha=0.7f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = value == selectedOption, borderColor = GlassBorder)
                )
            }
        }
    }
}
