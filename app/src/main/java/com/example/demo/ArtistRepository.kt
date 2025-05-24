//ArtistRepository.kt
package com.example.demo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.demo.models.Artist
import com.example.demo.models.Artwork
import com.example.demo.models.Category

class ArtistRepository {
    private val gson = GsonBuilder()
        .setLenient()
        .create()
        
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
        
    private val retrofit = Retrofit.Builder()
        .baseUrl("BACKEND_URL") // Replace with your backend url
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val api: ArtistApi = retrofit.create(ArtistApi::class.java)

    suspend fun searchArtists(query: String): List<Artist> {
        return try {
            val response = api.searchArtists(query)
            val responseString = response.string()
            Log.e("API_DEBUG", "${responseString}")
            val type = object : TypeToken<List<Artist>>() {}.type
            gson.fromJson(responseString, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error processing response: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getArtistDetails(artistId: String): Artist? {
        return try {
            val response = api.getArtistDetails(artistId)
            val responseString = response.string()
            Log.e("API_DEBUG", "${responseString}")
            val type = object : TypeToken<Artist>() {}.type
            gson.fromJson(responseString, type)
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error fetching artist details: ${e.message}")
            null
        }
    }

    suspend fun getArtworks(artistId: String): List<Artwork> {
        return try {
            val response = api.getArtworks(artistId)
            val responseString = response.string()
            Log.e("API_DEBUG", "${responseString}")
            val type = object : TypeToken<List<Artwork>>() {}.type
            gson.fromJson(responseString, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error fetching artworks: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCategories(artworkId: String): List<Category> {
        return try {
            val response = api.getCategories(artworkId)
            val responseString = response.string()
            Log.e("API_DEBUG", "${responseString}")
            val type = object : TypeToken<List<Category>>() {}.type
            gson.fromJson(responseString, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error fetching artworks: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSimilarArtists(artistId: String): List<Artist> {
        return try {
            val response = api.getSimilarArtists(artistId)
            val responseString = response.string()
            Log.e("API_DEBUG", "${responseString}")
            val type = object : TypeToken<List<Artist>>() {}.type
            gson.fromJson(responseString, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error fetching similar artists: ${e.message}")
            emptyList()
        }
    }
} 