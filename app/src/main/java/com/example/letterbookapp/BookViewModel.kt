package com.example.letterbookapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    var averageRating by mutableStateOf<Float?>(null)
        private set

    var ratingCount by mutableStateOf<Int?>(null)
        private set

    val libraryBooks = mutableStateListOf<BookDoc>()
    val readLaterBooks = mutableStateListOf<BookDoc>()
    val userReviews = mutableStateListOf<UserReview>()

    fun toggleAddToLibrary(book: BookDoc) {
        if (libraryBooks.any { it.key == book.key }) {
            libraryBooks.removeAll { it.key == book.key }
        } else {
            libraryBooks.add(book)
        }
    }

    fun isBookInLibrary(book: BookDoc): Boolean {
        return libraryBooks.any { it.key == book.key }
    }

    fun toggleReadLater(book: BookDoc) {
        if (readLaterBooks.any { it.key == book.key }) {
            readLaterBooks.removeAll { it.key == book.key }
        } else {
            readLaterBooks.add(book)
        }
    }

    fun isBookInReadLater(book: BookDoc): Boolean {
        return readLaterBooks.any { it.key == book.key }
    }

    fun addReview(book: BookDoc, rating: Float, comment: String) {
        val existingIndex = userReviews.indexOfFirst { it.bookKey == book.key }
        val newReview = UserReview(
            bookKey = book.key ?: "",
            bookTitle = book.title ?: "Untitled",
            rating = rating,
            comment = comment.ifBlank { null }
        )

        if (existingIndex != -1) {
            userReviews[existingIndex] = newReview
        } else {
            userReviews.add(newReview)
        }
    }

    fun getReviewForBook(bookKey: String?): UserReview? {
        return userReviews.find { it.bookKey == bookKey }
    }

    fun selectBook(book: BookDoc) {
        selectedBook = book
        bookDescription = "Loading description..."
        averageRating = null
        ratingCount = null

        val workId = book.key?.removePrefix("/works/")?.removePrefix("/books/")

        if (!workId.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getBookDetails(workId)
                    bookDescription = response.descriptionText
                } catch (e: Exception) {
                    bookDescription = "Description unavailable."
                }

                try {
                    val ratingResponse = RetrofitInstance.api.getBookRatings(workId)
                    averageRating = ratingResponse.summary?.average
                    ratingCount = ratingResponse.summary?.count
                } catch (e: Exception) {
                    averageRating = null
                    ratingCount = null
                }
            }
        } else {
            bookDescription = "No details available for this book."
        }
    }

    fun clearSelectedBook() {
        selectedBook = null
        bookDescription = null
        averageRating = null
        ratingCount = null
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