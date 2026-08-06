//package com.example.letterbookapp
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface ApiService {
//    @GET("search.json")
//    suspend fun searchBooks(
//        @Query("q") query: String,
//        @Query("limit") limit: Int = 20
//    ): BookSearchResponse
//}
//
//object RetrofitInstance {
//    private const val BASE_URL = "https://openlibrary.org/"
//
//    val api: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//}

package com.example.letterbookapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BookSearchResponse

    @GET("works/{workId}.json")
    suspend fun getBookDetails(
        @Path("workId") workId: String
    ): BookDetailResponse

    @GET("works/{workId}/ratings.json")
    suspend fun getBookRatings(
        @Path("workId") workId: String
    ): RatingResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://openlibrary.org/"

    val api: OpenLibraryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApi::class.java)
    }
}