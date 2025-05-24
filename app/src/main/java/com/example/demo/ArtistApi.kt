//ArtsyApi.kt
package com.example.demo

import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody

interface ArtistApi {
    @GET("artists/search")
    suspend fun searchArtists(@Query("keyword") query: String): ResponseBody

    @GET("artists")
    suspend fun getArtistDetails(@Query("id") artistId: String): ResponseBody

    @GET("artists/artworks")
    suspend fun getArtworks(@Query("id") artistId: String): ResponseBody

    @GET("artists/artworks/genes")
    suspend fun getCategories(@Query("id") artworkId: String): ResponseBody

    @GET("artists/similar")
    suspend fun getSimilarArtists(@Query("id") artistId: String): ResponseBody

} 