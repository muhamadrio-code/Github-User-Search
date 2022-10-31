package com.muhammadrio.githubuser.provider

import android.content.SearchRecentSuggestionsProvider

class SuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.muhammadrio.githubuser.provider.SuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}