package com.muhammadrio.githubuser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhammadrio.githubuser.SearchResponse
import com.muhammadrio.githubuser.model.QueryResult
import com.muhammadrio.githubuser.service.Retrofit
import retrofit2.Response
import java.io.IOException

class UserRepository {

    private val userApi = Retrofit.userApi

    private val _searchResponse = MutableLiveData<SearchResponse>()
    val searchResponse : LiveData<SearchResponse> = _searchResponse

    suspend fun searchUsers(q:String){
        runCatching {
            userApi.searchUsers(q)
        }.onFailure { e ->
            if (e is IOException) {
                e.printStackTrace()
            }
            _searchResponse.value = SearchResponse.OnFailed
        }.onSuccess {response ->
            handleResponse(response)
        }
    }

    private fun handleResponse(response: Response<QueryResult>){
        when (response.code()){
            in 200..300 -> handleResponseSuccess(response)
            304 -> _searchResponse.value = SearchResponse.OnFailed
            422 -> _searchResponse.value = SearchResponse.OnValidationFailed
            503 -> _searchResponse.value = SearchResponse.OnServiceUnavailable
        }
    }

    private fun handleResponseSuccess(response: Response<QueryResult>) {
        val body = response.body()
        body?.let { queryResult ->
            _searchResponse.value = SearchResponse.OnSuccess(queryResult)
        }
    }

}