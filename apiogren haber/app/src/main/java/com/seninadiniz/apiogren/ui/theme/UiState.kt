package com.seninadiniz.apiogren.ui.theme

import com.seninadiniz.apiogren.data.NewsItem

sealed class UiState {
    object Loading : UiState()
    data class Success(val news: List<NewsItem>) : UiState()
    data class Error(val message: String) : UiState()
}
