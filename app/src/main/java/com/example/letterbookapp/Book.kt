package com.example.letterbookapp

import com.google.gson.annotations.SerializedName

data class BookSearchResponse(
    @SerializedName("numFound") val numFound: Int? = 0,
    @SerializedName("docs") val books: List<BookDoc>? = emptyList()
)

data class BookDoc(
    @SerializedName("key") val key: String? = "",
    @SerializedName("title") val title: String? = "Untitled",
    @SerializedName("author_name") val authorNames: List<String>? = emptyList(),
    @SerializedName("first_publish_year") val publishYear: Int? = null,
    @SerializedName("cover_i") val coverId: Long? = null
) {
    val coverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }

    val primaryAuthor: String
        get() = authorNames?.firstOrNull() ?: "Unknown Author"
}