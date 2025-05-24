package com.example.demo.models
import java.util.Date

data class Favorite(
    val artistId: String,
    val artistName: String,
    val nationality: String?,
    val birthday: String?,
    val deathday: String?,
    val addedAt: Date
)