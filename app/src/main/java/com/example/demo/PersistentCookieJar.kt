package com.example.demo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.demo.MainActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar private constructor() : CookieJar {
    private val TAG = "PersistentCookieJar"
    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()
    private val gson: Gson = GsonBuilder().create()

    private val sharedPreferences: SharedPreferences by lazy {
        MainActivity.getInstance().getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
    }

    companion object {
        const val COOKIE_PREFS = "app_cookies"

        @Volatile
        private var instance: PersistentCookieJar? = null

        fun getInstance(): PersistentCookieJar {
            return instance ?: synchronized(this) {
                instance ?: PersistentCookieJar().also {
                    instance = it
                    it.loadFromPrefs()
                }
            }
        }
    }

    init {
        loadFromPrefs()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            Log.d(TAG, "Saving ${cookies.size} cookies for url: ${url.host}")
            cookieStore[url.host] = cookies
            saveToDisk()
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host] ?: emptyList()

        // Filter out expired cookies
        val validCookies = cookies.filter { !it.expiresAt.let { expiresAt -> expiresAt > 0 && expiresAt < System.currentTimeMillis() } }

        if (validCookies.size != cookies.size) {
            // Some cookies expired, update the store
            if (validCookies.isEmpty()) {
                cookieStore.remove(url.host)
            } else {
                cookieStore[url.host] = validCookies
            }
            saveToDisk()
        }

        Log.d(TAG, "Loading ${validCookies.size} cookies for url: ${url.host}")
        return validCookies
    }

    fun clear() {
        Log.d(TAG, "Clearing all cookies")
        cookieStore.clear()
        saveToDisk()
    }

    private fun saveToDisk() {
        try {
            val editor = sharedPreferences.edit()
            val serializedCookies = mutableMapOf<String, String>()

            cookieStore.forEach { (host, cookies) ->
                val serialized = gson.toJson(cookies)
                serializedCookies[host] = serialized
            }

            val serializedMap = gson.toJson(serializedCookies)
            editor.putString("cookies", serializedMap)
            editor.apply()

            Log.d(TAG, "Cookies saved to disk: ${cookieStore.size} domains")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cookies: ${e.message}")
        }
    }

    private fun loadFromPrefs() {
        try {
            val serializedMap = sharedPreferences.getString("cookies", null) ?: return

            val mapType = object : TypeToken<Map<String, String>>() {}.type
            val cookieMap: Map<String, String> = gson.fromJson(serializedMap, mapType)

            cookieMap.forEach { (host, serializedCookies) ->
                val listType = object : TypeToken<List<SerializableCookie>>() {}.type
                val cookieObjects: List<SerializableCookie> = gson.fromJson(serializedCookies, listType)

                val cookies = cookieObjects.mapNotNull { it.toCookie() }
                if (cookies.isNotEmpty()) {
                    cookieStore[host] = cookies
                }
            }

            Log.d(TAG, "Cookies loaded from disk: ${cookieStore.size} domains")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cookies: ${e.message}")
            // If there's an error loading cookies, clear everything to start fresh
            clear()
        }
    }

    // Helper class for cookie serialization with Gson
    private data class SerializableCookie(
        val name: String,
        val value: String,
        val expiresAt: Long,
        val domain: String,
        val path: String,
        val secure: Boolean,
        val httpOnly: Boolean,
        val hostOnly: Boolean
    ) {
        fun toCookie(): Cookie? {
            return try {
                Cookie.Builder()
                    .name(name)
                    .value(value)
                    .expiresAt(expiresAt)
                    .domain(domain)
                    .path(path)
                    .apply { if (secure) secure() }
                    .apply { if (httpOnly) httpOnly() }
                    .build()
            } catch (e: Exception) {
                Log.e("SerializableCookie", "Error converting to Cookie: ${e.message}")
                null
            }
        }

        companion object {
            fun fromCookie(cookie: Cookie): SerializableCookie {
                return SerializableCookie(
                    name = cookie.name,
                    value = cookie.value,
                    expiresAt = cookie.expiresAt,
                    domain = cookie.domain,
                    path = cookie.path,
                    secure = cookie.secure,
                    httpOnly = cookie.httpOnly,
                    hostOnly = cookie.hostOnly
                )
            }
        }
    }
}