package com.example.letterbookapp

import com.google.gson.annotations.SerializedName

data class BookSearchResponse(
    @SerializedName("numFound") val numFound: Int? = 0,
    @SerializedName("docs") val books: List<BookDoc>? = emptyList()
)

data class RatingResponse(
    @SerializedName("summary") val summary: RatingSummary?
)

data class RatingSummary(
    @SerializedName("average") val average: Float?,
    @SerializedName("count") val count: Int?
)

data class BookDoc(
    @SerializedName("key") val key: String? = "",
    @SerializedName("title") val title: String? = "Untitled",
    @SerializedName("author_name") val authorNames: List<String>? = emptyList(),
    @SerializedName("first_publish_year") val publishYear: Int? = null,
    @SerializedName("cover_i") val coverId: Long? = null,
    @SerializedName("publisher") val publishers: List<String>? = emptyList(),
    @SerializedName("subject") val subjects: List<String>? = emptyList(),
    @SerializedName("number_of_pages_median") val pageCount: Int? = null,
    @SerializedName("ratings_average") val rating: Double? = null
) {

    val largeCoverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
    val coverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
    val primaryAuthor: String
        get() = authorNames?.firstOrNull() ?: "Unknown Author"
}

data class BookDetailResponse(
    @SerializedName("description")
    val descriptionRaw: Any? = null,
    @SerializedName("subjects")
    val subjects: List<String>? = emptyList()
) {
    val descriptionText: String
        get() = when (descriptionRaw) {
            is String -> descriptionRaw
            is Map<*, *> -> descriptionRaw["value"]?.toString() ?: "No description available."
            else -> "No description available for this book."
        }
}

data class UserReview(
    val bookKey: String,
    val bookTitle: String,
    val rating: Float,
    val comment: String? = null
)