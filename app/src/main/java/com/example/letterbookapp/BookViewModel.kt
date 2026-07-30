package com.example.letterbookapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface BookUiState {
    object Initial : BookUiState
    object Loading : BookUiState
    data class Success(val books: List<BookDoc>) : BookUiState
    data class Error(val message: String) : BookUiState
}

class BookViewModel : ViewModel() {
    var uiState: BookUiState by mutableStateOf(BookUiState.Initial)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun searchBooks(query: String = searchQuery) {
        if (query.isBlank()) return

        viewModelScope.launch {
            uiState = BookUiState.Loading
            try {
                val response = RetrofitInstance.api.searchBooks(query)
                val bookList = response.books ?: emptyList()

                uiState = if (bookList.isEmpty()) {
                    BookUiState.Error("No books found for '$query'")
                } else {
                    BookUiState.Success(bookList)
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Search error", e)
                uiState = BookUiState.Error("Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
}