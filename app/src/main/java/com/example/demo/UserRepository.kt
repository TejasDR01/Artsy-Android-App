//UserRepository.kt
package com.example.demo

import android.util.Log
import com.example.demo.models.User
import com.example.demo.models.Favorite
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.example.demo.ui.SnackbarManager
import com.example.demo.ui.UserViewModel



class UserRepository {
    private val gson = GsonBuilder().setLenient().create()

    private val cookieJar = PersistentCookieJar.getInstance()
    private val snackbar = SnackbarManager.getInstance()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .cookieJar(cookieJar)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "BACKEND_URL" // Replace with your backend url
    private val sharedPreferences: SharedPreferences by lazy {
        MainActivity.getInstance().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
    }
    companion object {
        const val USER_PREFS = "user_data"
        const val KEY_USER_DATA = "user_data"
    }

    suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("${baseUrl}auth/login")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("UserRepository", "Login response: $responseBody")

                // Parse user from response
                val type = object : TypeToken<User>() {}.type
                val user = gson.fromJson<User>(responseBody, type)
                if (user != null) {
                    saveUserToLocalStorage(user)
                }
                return@withContext user
            } else {
                Log.e("UserRepository", "Login failed: ${response.code} ${response.message}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Login exception: ${e.message}")
            throw e
        }
    }

    suspend fun register(name: String, email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", password)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("${baseUrl}auth/register")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("UserRepository", "Register response: $responseBody")

                // Parse user from response
                val type = object : TypeToken<User>() {}.type
                val user = gson.fromJson<User>(responseBody, type)
                if (user != null) {
                    saveUserToLocalStorage(user)
                }
                return@withContext user
            } else {
                Log.e("UserRepository", "Registration failed: ${response.code} ${response.message}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration exception: ${e.message}")
            throw e
        }
    }

    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${baseUrl}auth/logout")
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // Clear cookies on successful logout
                clearUserFromLocalStorage()
                cookieJar.clear()
                return@withContext true
            } else {
                Log.e("UserRepository", "Logout failed: ${response.code} ${response.message}")
                clearUserFromLocalStorage()
                cookieJar.clear()
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Logout exception: ${e.message}")
            throw e
        }
    }

    suspend fun delete(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${baseUrl}auth/delete")
                .delete()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // Clear cookies on successful logout
                clearUserFromLocalStorage()
                cookieJar.clear()
                return@withContext true
            } else {
                Log.e("UserRepository", "Delete failed: ${response.code} ${response.message}")
                if(response.code == 403) {
                    clearUserFromLocalStorage()
                    cookieJar.clear()
                    snackbar.showMessage("Session Expired", true)
                }
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Delete exception: ${e.message}")
            throw e
        }
    }

    suspend fun getfavorites(): List<Favorite> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${baseUrl}favorites")
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("UserRepository", "getFavorites response: $responseBody")

                // Parse user from response
                val type = object : TypeToken<List<Favorite>>() {}.type
                val favorites = gson.fromJson<List<Favorite>>(responseBody, type)
                return@withContext favorites
            } else {
                Log.e("UserRepository", "getFavorites failed: ${response.code} ${response.message}")
                if(response.code == 403) {
                    clearUserFromLocalStorage()
                    cookieJar.clear()
                    snackbar.showMessage("Session Expired", true)
                }
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "getFavorites exception: ${e.message}")
            throw e
        }
    }

    suspend fun removefavorite(artistId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${baseUrl}favorites?id=$artistId")
                .delete()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                return@withContext true
            } else {
                Log.e("UserRepository", "removeFavorite failed: ${response.code} ${response.message}")
                if(response.code == 403) {
                    clearUserFromLocalStorage()
                    cookieJar.clear()
                    snackbar.showMessage("Session Expired", true)
                }
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "removeFavorite exception: ${e.message}")
            throw e
        }
    }

    suspend fun addfavorite(artistId: String): Favorite? = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("artistId", artistId)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("${baseUrl}favorites")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("UserRepository", "addFavorite response: $responseBody")

                // Parse user from response
                val type = object : TypeToken<Favorite>() {}.type
                val favorite = gson.fromJson<Favorite>(responseBody, type)
                return@withContext favorite
            } else {
                Log.e("UserRepository", "addFavorite failed: ${response.code} ${response.message}")
                if(response.code == 403) {
                    clearUserFromLocalStorage()
                    cookieJar.clear()
                    snackbar.showMessage("Session Expired", true)
                }
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "addFavorite exception: ${e.message}")
            throw e
        }
    }

    fun getUserFromLocalStorage(): User? {
        try {
            val userData = sharedPreferences.getString(KEY_USER_DATA, null) ?: return null
            return gson.fromJson(userData, User::class.java)
        } catch (e: Exception) {
            Log.e("Debug", "Error getting user from local storage: ${e.message}")
            return null
        }
    }

    // Save user data to local storage
    private fun saveUserToLocalStorage(user: User) {
        try {
            val userData = gson.toJson(user)
            sharedPreferences.edit().putString(KEY_USER_DATA, userData).apply()
            Log.d("Debug", "User data saved to local storage")
        } catch (e: Exception) {
            Log.e("Debug", "Error saving user to local storage: ${e.message}")
        }
    }

    // Clear user data from local storage
    private fun clearUserFromLocalStorage() {
        sharedPreferences.edit().remove(KEY_USER_DATA).apply()
        Log.d("Debug", "User data cleared from local storage")
    }
}