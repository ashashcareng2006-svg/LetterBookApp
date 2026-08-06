package com.example.letterbookapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.letterbookapp.ui.theme.LetterBookAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetterBookAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookAppNavigation()
                }
            }
        }
    }
}

@Composable
fun BookAppNavigation(
    viewModel: BookViewModel = viewModel()
) {
    val selectedBook = viewModel.selectedBook

    if (selectedBook != null) {
        BookDetailScreen(
            book = selectedBook,
            description = viewModel.bookDescription,
            onBackClick = { viewModel.clearSelectedBook() }
        )
    } else {
        BookSearchScreen(viewModel = viewModel)
    }
}

@Composable
fun BookSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: BookViewModel = viewModel()
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "LetterBookApp",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.onQueryChange(it) },
                    label = { Text("Search by title or author") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { viewModel.searchBooks() }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.searchBooks() },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Search")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // State Display
            when (val state = viewModel.uiState) {
                is BookUiState.Initial -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Search for a book above to get started!")
                    }
                }
                is BookUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BookUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is BookUiState.Success -> {
                    // Explicit type specified to fix 'Cannot infer type for value parameter book'
                    BookResultsList(
                        books = state.books,
                        onBookClick = { book: BookDoc -> viewModel.selectBook(book) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookResultsList(
    books: List<BookDoc>,
    onBookClick: (BookDoc) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Featured Selection",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Explicit type specified for loop variable
                items(books.take(5)) { book: BookDoc ->
                    FeaturedBookCard(
                        book = book,
                        onClick = { onBookClick(book) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "All Results",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Explicit type specified for loop variable
        items(books) { book: BookDoc ->
            BookItemCard(
                book = book,
                onClick = { onBookClick(book) }
            )
        }
    }
}

@Composable
fun FeaturedBookCard(
    book: BookDoc,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.title ?: "Untitled",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun BookItemCard(
    book: BookDoc,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(width = 60.dp, height = 90.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = book.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Author: ${book.primaryAuthor}",
                    style = MaterialTheme.typography.bodyMedium
                )
                book.publishYear?.let { year ->
                    Text(
                        text = "Published: $year",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}