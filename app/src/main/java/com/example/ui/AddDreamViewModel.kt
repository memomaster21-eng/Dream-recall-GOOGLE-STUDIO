package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Dream
import com.example.data.DreamRepository
import kotlinx.coroutines.launch

class AddDreamViewModel(private val repository: DreamRepository) : ViewModel() {

    fun saveDream(
        title: String,
        content: String,
        mood: String,
        vividness: Float,
        isLucid: Boolean,
        isNightmare: Boolean,
        tags: String
    ) {
        val dream = Dream(
            title = title,
            content = content,
            mood = mood,
            vividness = vividness.toInt(),
            isLucid = isLucid,
            isNightmare = isNightmare,
            tags = tags,
            sleepDurationMinutes = 0
        )
        viewModelScope.launch {
            repository.insert(dream)
        }
    }
}

class AddDreamViewModelFactory(private val repository: DreamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDreamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddDreamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
