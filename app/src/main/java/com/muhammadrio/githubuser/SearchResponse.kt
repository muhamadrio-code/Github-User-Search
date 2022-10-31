package com.muhammadrio.githubuser

import com.muhammadrio.githubuser.model.QueryResult

sealed class SearchResponse {
    data class OnSuccess(val data: QueryResult) : SearchResponse() {
        val dataExist = data.items.isNotEmpty()
    }
    object OnValidationFailed : SearchResponse()
    object OnServiceUnavailable : SearchResponse()
    object OnFailed : SearchResponse()
}
