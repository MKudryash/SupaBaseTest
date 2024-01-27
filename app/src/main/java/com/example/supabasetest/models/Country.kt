package com.example.supabasetest.models

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val id: Int,
    val name: String,
)