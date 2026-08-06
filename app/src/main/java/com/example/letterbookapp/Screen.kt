package com.example.letterbookapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen(route = "home", title = "Home", icon = Icons.Default.Home)
    object Search : Screen(route = "search", title = "Search", icon = Icons.Default.Search)
    object Library : Screen(route = "library", title = "Library", icon = Icons.AutoMirrored.Filled.LibraryBooks)
    object Profile : Screen(route = "profile", title = "Profile", icon = Icons.Default.Person)
}