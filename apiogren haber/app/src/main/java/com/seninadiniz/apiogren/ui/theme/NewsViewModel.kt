package com.seninadiniz.apiogren.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seninadiniz.apiogren.data.NewsRepository
import com.seninadiniz.apiogren.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository(NetworkModule.newsApi)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchTop()
    }

    fun fetchTop() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getTopHeadlines()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Bilinmeyen hata") }
            )
        }
    }

    fun search(query: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.search(query)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Bilinmeyen hata") }
            )
        }
    }
}