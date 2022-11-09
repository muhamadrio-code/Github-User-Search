package com.muhammadrio.githubuser.network

sealed class QueryStatus {
    object OnEmpty : QueryStatus()
    object OnLoading : QueryStatus()
    object OnSuccess : QueryStatus()
    data class OnFailure(val errorMessage: ErrorMessage) : QueryStatus()
}