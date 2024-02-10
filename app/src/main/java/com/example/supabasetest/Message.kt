package com.example.supabasetest

import kotlinx.serialization.Serializable

@Serializable
data class Message(val content: String, val sender: String)

