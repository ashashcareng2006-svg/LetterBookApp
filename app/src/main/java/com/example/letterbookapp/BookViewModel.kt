package com.example.letterbookapp

// import android.util.Log
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

    var selectedBook by mutableStateOf<BookDoc?>(null)
        private set

    var bookDescription by mutableStateOf<String?>("Loading description...")
        private set

    fun selectBook(book: BookDoc) {
        selectedBook = book
        bookDescription = "Loading description..."

        val workId = book.key?.removePrefix("/works/")?.removePrefix("/books/")

        if (!workId.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getBookDetails(workId)
                    bookDescription = response.descriptionText
                } catch (e:Exception) {
                    bookDescription = "Description unavailable."
                }
            }
        } else {
            bookDescription = "No details available for this book."
        }
    }

    fun clearSelectedBook() {
        selectedBook = null
        bookDescription = null
    }

    var uiState: BookUiState by mutableStateOf(BookUiState.Initial)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun searchBooks() {
        if (searchQuery.isBlank()) return

        uiState = BookUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchBooks(searchQuery)
                val results = response.books ?: emptyList()
                uiState = if (results.isEmpty()) {
                    BookUiState.Error("No books found for '$searchQuery'")
                } else {
                    BookUiState.Success(results)
                }

            } catch (e: Exception) {
                uiState = BookUiState.Error("Failed to fetch books: ${e.localizedMessage}")
            }
        }
    }
}