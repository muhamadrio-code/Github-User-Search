package com.muhammadrio.githubuser.network


sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val errorMessage:ErrorMessage, val throwable: Throwable? = null) : Result<Nothing>()
}