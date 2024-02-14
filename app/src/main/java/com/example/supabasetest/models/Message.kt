package com.example.supabasetest.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int = 0,
    val created_at: String? = null,
    val chat_id: Int = 0,
    val author_id: String? = null,
    val type: String,
    val seen: Boolean? = false,
    val content: String? = null
)