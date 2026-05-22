package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

class AddDreamViewModel(private val repository: DreamRepository) : ViewModel() {
    fun saveDream(title: String, content: String, isLucid: Boolean, isNightmare: Boolean) {
        viewModelScope.launch {
            repository.insertDream(
                Dream(
                    title = title,
                    content = content,
                    isLucid = isLucid,
                    isNightmare = isNightmare
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حلم جديد") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان الحلم (اختياري)", color = GrayText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftCyan,
                    unfocusedBorderColor = DeepNavy,
                    focusedTextColor = WarmWhite,
                    unfocusedTextColor = WarmWhite
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("ماذا حدث بالتفصيل؟", color = GrayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftCyan,
                    unfocusedBorderColor = DeepNavy,
                    focusedTextColor = WarmWhite,
                    unfocusedTextColor = WarmWhite
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isLucid,
                    onCheckedChange = { isLucid = it },
                    colors = CheckboxDefaults.colors(checkedColor = SoftCyan)
                )
                Text("حلم جلي (كنت واعياً أثناء الحلم)", color = WarmWhite)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isNightmare,
                    onCheckedChange = { isNightmare = it },
                    colors = CheckboxDefaults.colors(checkedColor = DimLavender)
                )
                Text("كابوس / حلم مزعج", color = WarmWhite)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        viewModel.saveDream(title, content, isLucid, isNightmare)
                        onSave()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SoftCyan, contentColor = DeepBlack),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("حفظ الحلم")
            }
        }
    }
}
