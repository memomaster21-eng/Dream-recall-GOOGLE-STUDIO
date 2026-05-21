package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Dream
import com.example.data.DreamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DreamDetailViewModel(private val repository: DreamRepository, private val dreamId: Int) : ViewModel() {
    private val _dream = MutableStateFlow<Dream?>(null)
    val dream: StateFlow<Dream?> = _dream.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getDream(dreamId).collect {
                _dream.value = it
            }
        }
    }

    fun deleteDream() {
        viewModelScope.launch {
            _dream.value?.let { 
                repository.delete(it) 
            }
        }
    }
}

class DreamDetailViewModelFactory(
    private val repository: DreamRepository,
    private val dreamId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DreamDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DreamDetailViewModel(repository, dreamId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
