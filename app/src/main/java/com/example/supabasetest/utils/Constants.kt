package com.example.supabasetest.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.ExternalAuthAction
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest

object Constants {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://settlovuuvlcbrhcydxf.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNldHRsb3Z1dXZsY2JyaGN5ZHhmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDYxOTEwNDUsImV4cCI6MjAyMTc2NzA0NX0.72gGEF3pi943jCSxcIJk7Urbv6BFl1iNAm61uEQ5aVU"
    ) {
        install(Auth)
        install(Postgrest)
    }
}